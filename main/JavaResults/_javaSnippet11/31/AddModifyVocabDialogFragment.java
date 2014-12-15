package org.easycomm.config;

import org.easycomm.R;
import org.easycomm.config.LinkChooserDialogFragment.LinkChooserDialogListener;
import org.easycomm.config.VocabChooserDialogFragment.VocabChooserDialogListener;
import org.easycomm.model.VocabData;
import org.easycomm.model.VocabDatabase;
import org.easycomm.model.VocabReader;
import org.easycomm.model.graph.Folder;
import org.easycomm.model.graph.Leaf;
import org.easycomm.model.graph.Link;
import org.easycomm.model.graph.Vocab;
import org.easycomm.model.visitor.VocabVisitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class AddModifyVocabDialogFragment extends DialogFragment implements
		VocabChooserDialogListener,
		LinkChooserDialogListener {

	public interface AddVocabDialogListener {
		public void onAddVocabDialogPositiveClick(String id, VocabData data, String followupFolderID);
	}

	public static final String ARG_VOCAB_ID = "vocabID";
	private String mSelectedVocabID;

	public static final String ARG_FOLDER_ID = "folderID";
	private String mFollowupFolderID;

	private VocabDatabase mVocabDB;
	private VocabReader mVocabReader; 
	private AddVocabDialogListener mListener;
	
	//Instance states
	public static final String DISPLAY_TEXT = "displayText";
	private String mDisplayText;
	
	public static final String SPEECH_CHECKBOX = "speechCheckBox";
	private boolean mSpeakCB = true;
	
	public static final String SPEECH_TEXT = "speechText";
	private String mSpeechText = "";
	
	public static final String NEW_IMAGE_FILENAME = "newImageFilename";
	private String mNewImageFilename;
	
	public static final String NEW_FOLLOWUP_FOLDER_ID = "newFollowupFolderID";
	private String mNewFollowupFolderID;
	
	public static final String REQUIRE_FOLLOWUP_FOLDER_ID = "requireFollowupFolderID";
	private boolean mRequireFollowupFolderID;
	
	public static final String DITTO = "ditto";
	private boolean mDitto;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (AddVocabDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement AddVocabDialogListener");
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(DISPLAY_TEXT, mDisplayText);
		outState.putBoolean(SPEECH_CHECKBOX, mSpeakCB);
		outState.putString(SPEECH_TEXT, mSpeechText);
		outState.putString(NEW_IMAGE_FILENAME, mNewImageFilename);
		outState.putString(NEW_FOLLOWUP_FOLDER_ID, mNewFollowupFolderID);
		outState.putBoolean(REQUIRE_FOLLOWUP_FOLDER_ID, mRequireFollowupFolderID);
		outState.putBoolean(DITTO, mDitto);
		super.onSaveInstanceState(outState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// setup or restore instance variables
		mVocabDB = VocabDatabase.getInstance(getResources().getAssets());
		mVocabReader = VocabReader.getInstance(getResources().getAssets());

		mSelectedVocabID = getArguments().getString(ARG_VOCAB_ID);
		mFollowupFolderID = getArguments().getString(ARG_FOLDER_ID);
			
		if (savedInstanceState != null) {
			mDisplayText             = savedInstanceState.getString(DISPLAY_TEXT);
			mSpeakCB	             = savedInstanceState.getBoolean(SPEECH_CHECKBOX);
			mSpeechText              = savedInstanceState.getString(SPEECH_TEXT);
			mNewImageFilename        = savedInstanceState.getString(NEW_IMAGE_FILENAME);
			mNewFollowupFolderID     = savedInstanceState.getString(NEW_FOLLOWUP_FOLDER_ID);
			mRequireFollowupFolderID = savedInstanceState.getBoolean(REQUIRE_FOLLOWUP_FOLDER_ID);
			mDitto                   = savedInstanceState.getBoolean(DITTO);
		} else {
			Vocab selectedVocab = mVocabDB.getVocab(mSelectedVocabID);
			VocabData selectedVocabData = selectedVocab.getData();
			
			if (selectedVocabData != null) {
				mDisplayText = selectedVocabData.getDisplayText();
				mSpeechText = selectedVocabData.getSpeechText();
				if (mSpeechText == null) {
					mSpeakCB = false;
				} else {
					mSpeakCB = true;
				}
				mNewImageFilename = selectedVocabData.getFilename();
			}
			
			if (mFollowupFolderID != null) {
				mNewFollowupFolderID = mFollowupFolderID;
				mRequireFollowupFolderID = true;
			}
			
			mDitto = true;
		}
		

		//Inflate view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_vocab_add, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				sendbackVocabData();
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
			}
		});

		//Attach listeners and adapters
		final EditText displayText = (EditText) view.findViewById(R.id.display_text);
		final CheckBox speakCB = (CheckBox) view.findViewById(R.id.speak_checkbox);
		final EditText speechText = (EditText) view.findViewById(R.id.speech_text);
		Button imageChooseButton = (Button) view.findViewById(R.id.image_choose_button);
		ImageView image = (ImageView) view.findViewById(R.id.vocab_image_chosen);
		final View linkLabel = view.findViewById(R.id.link_label);
		final View linkContent = view.findViewById(R.id.link_content);
		Button linkChooseButton = (Button) view.findViewById(R.id.link_choose_button);
		ImageView imageLink = (ImageView) view.findViewById(R.id.folder_image_chosen);
		
		displayText.setText(mDisplayText);
		speakCB.setChecked(mSpeakCB);
		speechText.setEnabled(mSpeakCB);
		if (mSpeakCB) {
			speechText.setText(mSpeechText);
		}
		
		if (mNewImageFilename != null) {
			image.setImageDrawable(mVocabReader.getVocabData(mNewImageFilename).getImage());
		}
		
		if (mNewFollowupFolderID != null) {
			imageLink.setImageDrawable(mVocabDB.getVocabData(mNewFollowupFolderID).getImage());
		}

		mVocabDB.getVocab(mSelectedVocabID).accept(new VocabVisitor() {
			@Override
			public void visit(Leaf v) {
				builder.setTitle("Add/Modify a Vocab");
				linkLabel.setVisibility(View.GONE);
				linkContent.setVisibility(View.GONE);
			}

			@Override
			public void visit(Folder v) {
				builder.setTitle("Add/Modify a Folder");
				linkLabel.setVisibility(View.GONE);
				linkContent.setVisibility(View.GONE);
			}

			@Override
			public void visit(Link v) {
				builder.setTitle("Add/Modify a Link");
				mRequireFollowupFolderID = true;
			}			
		});
		
		TextWatcher displayTextWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if(speakCB.isChecked() && mDitto){
					speechText.setText(s.toString());
				}				
				
				validate();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {	
			}			
		};
		
		TextWatcher speechTextWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String displayStr = displayText.getText().toString();
				String speechStr = speechText.getText().toString();
				if (!speechStr.equals(displayStr)) {
					mDitto = false;
				}
				
				validate();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}			
		};
		
		
		displayText.addTextChangedListener(displayTextWatcher);
		speechText.addTextChangedListener(speechTextWatcher);

		speakCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean checked = ((CheckBox) v).isChecked();
				speechText.setEnabled(checked);
				if(!checked) {
					speechText.setText("");
				}
				
				validate();
			}			
		});

		imageChooseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VocabChooserDialogFragment frag = new VocabChooserDialogFragment();
				frag.setTargetFragment(AddModifyVocabDialogFragment.this, 0);
				frag.show(getFragmentManager(), "VocabChooser");		
			}
		});

		linkChooseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinkChooserDialogFragment frag = new LinkChooserDialogFragment();
				frag.setTargetFragment(AddModifyVocabDialogFragment.this, 0);
				frag.show(getFragmentManager(), "LinkChooser");		
			}
		});

		//Populate content for Modify
		AlertDialog dialog = builder.create();
		dialog.show();
		validate(dialog);		
		return dialog;
	}

	@Override
	public void onLinkChooserDialogPositiveClick(Folder folder) {
		Drawable image = folder.getData().getImage();
		ImageView iv = (ImageView) getDialog().findViewById(R.id.folder_image_chosen);
		iv.setImageDrawable(image);
		mNewFollowupFolderID = folder.getID();
		validate();
	}

	@Override
	public void onVocabChooserDialogPositiveClick(VocabData data) {
		mNewImageFilename = data.getFilename();
		Drawable image = data.getImage();
		ImageView iv = (ImageView) getDialog().findViewById(R.id.vocab_image_chosen);
		iv.setImageDrawable(image);
		validate();
	}	

	private void validate() {
		validate((AlertDialog) getDialog());
	}

	private void validate(AlertDialog dialog) {
		if (dialog == null) {
			return;
		}

		boolean enable = true;

		EditText displayText = (EditText) dialog.findViewById(R.id.display_text);
		EditText speechText = (EditText) dialog.findViewById(R.id.speech_text);
		CheckBox speakCB = (CheckBox) dialog.findViewById(R.id.speak_checkbox);
		Button posButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		if (displayText.getText().length() == 0) {
			enable = false;
		}
		if (speakCB.isChecked() && speechText.getText().length() == 0) {
			enable = false;
		}
		if (mNewImageFilename == null) {
			enable = false;
		}		
		if (mRequireFollowupFolderID && mNewFollowupFolderID == null) {
			enable = false;
		}

		posButton.setEnabled(enable);
	}


	private void sendbackVocabData() {
		VocabData data = setupVocabData();		
		mListener.onAddVocabDialogPositiveClick(mSelectedVocabID, data, mNewFollowupFolderID);
	}

	private VocabData setupVocabData() {
		EditText displayTextView = (EditText) getDialog().findViewById(R.id.display_text);
		String displayText = displayTextView.getText().toString().trim();
		String speechText = null;
		CheckBox speakCB = (CheckBox) getDialog().findViewById(R.id.speak_checkbox);
		if (speakCB.isChecked()) {
			EditText speechTextView = (EditText) getDialog().findViewById(R.id.speech_text);
			speechText = speechTextView.getText().toString().trim();
		}

		ImageView vocabImageView = (ImageView) getDialog().findViewById(R.id.vocab_image_chosen);
		Drawable vocabImage = vocabImageView.getDrawable();

		return new VocabData(displayText, speechText, mNewImageFilename, vocabImage);
	}

}