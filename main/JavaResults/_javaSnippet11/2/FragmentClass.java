package com.radioactivefri.pathfindercharactersheet;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.concurrent.ConcurrentHashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentClass.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentClass#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FragmentClass extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String[] SKILL_NAMES = new String[] {"acrobatics", "appraise", "bluff", "climb", "craft", "diplomacy", "disableDevice", "disguise", "escapeArtist", "fly", "handleAnimal", "heal", "intimidate", "arcana", "dungeoneering", "engineering", "geography", "history", "local", "nature", "nobility", "planes", "religion", "linguistics", "perception", "perform", "profession", "ride", "senseMotive", "slightOfHand", "spellcraft", "stealth", "survival", "swim"};
    private View v;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Spinner classSpin;

    //CHECK BOXES
    CheckBox acrobaticsCB;
    CheckBox appraiseCB;
    CheckBox bluffCB;
    CheckBox climbCB;
    CheckBox craftCB;
    CheckBox diplomacyCB;
    CheckBox disableDeviceCB;
    CheckBox disguiseCB;
    CheckBox escapeArtistCB;
    CheckBox flyCB;
    CheckBox handleAnimalCB;
    CheckBox healCB;
    CheckBox intimidateCB;
    CheckBox arcanaCB;
    CheckBox dungeoneeringCB;
    CheckBox engineeringCB;
    CheckBox geographyCB;
    CheckBox historyCB;
    CheckBox localCB;
    CheckBox natureCB;
    CheckBox nobilityCB;
    CheckBox planesCB;
    CheckBox religionCB;
    CheckBox linguisticsCB;
    CheckBox perceptionCB;
    CheckBox performCB;
    CheckBox professionCB;
    CheckBox rideCB;
    CheckBox senseMotiveCB;
    CheckBox slightOfHandCB;
    CheckBox spellcraftCB;
    CheckBox stealthCB;
    CheckBox survivalCB;
    CheckBox swimCB;

    //TOTAL BONUSES
    EditText acrobaticsTB;
    EditText appraiseTB;
    EditText bluffTB;
    EditText climbTB;
    EditText craftTB;
    EditText diplomacyTB;
    EditText disableDeviceTB;
    EditText disguiseTB;
    EditText escapeArtistTB;
    EditText flyTB;
    EditText handleAnimalTB;
    EditText healTB;
    EditText intimidateTB;
    EditText arcanaTB;
    EditText dungeoneeringTB;
    EditText engineeringTB;
    EditText geographyTB;
    EditText historyTB;
    EditText localTB;
    EditText natureTB;
    EditText nobilityTB;
    EditText planesTB;
    EditText religionTB;
    EditText linguisticsTB;
    EditText perceptionTB;
    EditText performTB;
    EditText professionTB;
    EditText rideTB;
    EditText senseMotiveTB;
    EditText slightOfHandTB;
    EditText spellcraftTB;
    EditText stealthTB;
    EditText survivalTB;
    EditText swimTB;

    //ABILITY MOD
    EditText acrobaticsAM;
    EditText appraiseAM;
    EditText bluffAM;
    EditText climbAM;
    EditText craftAM;
    EditText diplomacyAM;
    EditText disableDeviceAM;
    EditText disguiseAM;
    EditText escapeArtistAM;
    EditText flyAM;
    EditText handleAnimalAM;
    EditText healAM;
    EditText intimidateAM;
    EditText arcanaAM;
    EditText dungeoneeringAM;
    EditText engineeringAM;
    EditText geographyAM;
    EditText historyAM;
    EditText localAM;
    EditText natureAM;
    EditText nobilityAM;
    EditText planesAM;
    EditText religionAM;
    EditText linguisticsAM;
    EditText perceptionAM;
    EditText performAM;
    EditText professionAM;
    EditText rideAM;
    EditText senseMotiveAM;
    EditText slightOfHandAM;
    EditText spellcraftAM;
    EditText stealthAM;
    EditText survivalAM;
    EditText swimAM;

    //RANKS
    EditText acrobaticsR;
    EditText appraiseR;
    EditText bluffR;
    EditText climbR;
    EditText craftR;
    EditText diplomacyR;
    EditText disableDeviceR;
    EditText disguiseR;
    EditText escapeArtistR;
    EditText flyR;
    EditText handleAnimalR;
    EditText healR;
    EditText intimidateR;
    EditText arcanaR;
    EditText dungeoneeringR;
    EditText engineeringR;
    EditText geographyR;
    EditText historyR;
    EditText localR;
    EditText natureR;
    EditText nobilityR;
    EditText planesR;
    EditText religionR;
    EditText linguisticsR;
    EditText perceptionR;
    EditText performR;
    EditText professionR;
    EditText rideR;
    EditText senseMotiveR;
    EditText slightOfHandR;
    EditText spellcraftR;
    EditText stealthR;
    EditText survivalR;
    EditText swimR;

    //MISCELANEOUS MOD
    EditText acrobaticsMM;
    EditText appraiseMM;
    EditText bluffMM;
    EditText climbMM;
    EditText craftMM;
    EditText diplomacyMM;
    EditText disableDeviceMM;
    EditText disguiseMM;
    EditText escapeArtistMM;
    EditText flyMM;
    EditText handleAnimalMM;
    EditText healMM;
    EditText intimidateMM;
    EditText arcanaMM;
    EditText dungeoneeringMM;
    EditText engineeringMM;
    EditText geographyMM;
    EditText historyMM;
    EditText localMM;
    EditText natureMM;
    EditText nobilityMM;
    EditText planesMM;
    EditText religionMM;
    EditText linguisticsMM;
    EditText perceptionMM;
    EditText performMM;
    EditText professionMM;
    EditText rideMM;
    EditText senseMotiveMM;
    EditText slightOfHandMM;
    EditText spellcraftMM;
    EditText stealthMM;
    EditText survivalMM;
    EditText swimMM;




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentClass.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentClass newInstance(String param1, String param2) {
        FragmentClass fragment = new FragmentClass();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FragmentClass() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (savedInstanceState == null) {

        }
        else {
            classSpin.setSelection((savedInstanceState.getInt("class")));

            //checkboxes
            acrobaticsCB.setChecked(savedInstanceState.getBoolean("acrobaticsCB"));
            appraiseCB.setChecked(savedInstanceState.getBoolean("appraiseCB"));
            bluffCB.setChecked(savedInstanceState.getBoolean("bluffCB"));
            climbCB.setChecked(savedInstanceState.getBoolean("climbCB"));
            craftCB.setChecked(savedInstanceState.getBoolean("craftCB"));
            diplomacyCB.setChecked(savedInstanceState.getBoolean("diplomacyCB"));
            disableDeviceCB.setChecked(savedInstanceState.getBoolean("disableDeviceCB"));
            disguiseCB.setChecked(savedInstanceState.getBoolean("disguiseCB"));
            escapeArtistCB.setChecked(savedInstanceState.getBoolean("escapeArtistCB"));
            flyCB.setChecked(savedInstanceState.getBoolean("flyCB"));
            handleAnimalCB.setChecked(savedInstanceState.getBoolean("handleAnimalCB"));
            healCB.setChecked(savedInstanceState.getBoolean("healCB"));
            intimidateCB.setChecked(savedInstanceState.getBoolean("intimidateCB"));
            arcanaCB.setChecked(savedInstanceState.getBoolean("arcanaCB"));
            dungeoneeringCB.setChecked(savedInstanceState.getBoolean("dungeoneeringCB"));
            engineeringCB.setChecked(savedInstanceState.getBoolean("engineeringCB"));
            geographyCB.setChecked(savedInstanceState.getBoolean("geographyCB"));
            historyCB.setChecked(savedInstanceState.getBoolean("historyCB"));
            localCB.setChecked(savedInstanceState.getBoolean("localCB"));
            natureCB.setChecked(savedInstanceState.getBoolean("natureCB"));
            nobilityCB.setChecked(savedInstanceState.getBoolean("nobilityCB"));
            planesCB.setChecked(savedInstanceState.getBoolean("planesCB"));
            religionCB.setChecked(savedInstanceState.getBoolean("religionCB"));
            linguisticsCB.setChecked(savedInstanceState.getBoolean("linguisticsCB"));
            perceptionCB.setChecked(savedInstanceState.getBoolean("perceptionCB"));
            performCB.setChecked(savedInstanceState.getBoolean("performCB"));
            professionCB.setChecked(savedInstanceState.getBoolean("professionCB"));
            rideCB.setChecked(savedInstanceState.getBoolean("rideCB"));
            senseMotiveCB.setChecked(savedInstanceState.getBoolean("senseMotiveCB"));
            slightOfHandCB.setChecked(savedInstanceState.getBoolean("slightOfHandCB"));
            spellcraftCB.setChecked(savedInstanceState.getBoolean("spellcraftCB"));
            stealthCB.setChecked(savedInstanceState.getBoolean("stealthCB"));
            survivalCB.setChecked(savedInstanceState.getBoolean("survivalCB"));
            swimCB.setChecked(savedInstanceState.getBoolean("swimCB"));

            //total bonuses
            acrobaticsTB.setText(savedInstanceState.getString("acrobaticsTB"));
            appraiseTB.setText(savedInstanceState.getString("appraiseTB"));
            bluffTB.setText(savedInstanceState.getString("bluffTB"));
            climbTB.setText(savedInstanceState.getString("climbTB"));
            craftTB.setText(savedInstanceState.getString("craftTB"));
            diplomacyTB.setText(savedInstanceState.getString("diplomacyTB"));
            disableDeviceTB.setText(savedInstanceState.getString("disableDeviceTB"));
            disguiseTB.setText(savedInstanceState.getString("disguiseTB"));
            escapeArtistTB.setText(savedInstanceState.getString("escapeArtistTB"));
            flyTB.setText(savedInstanceState.getString("flyTB"));
            handleAnimalTB.setText(savedInstanceState.getString("handleAnimalTB"));
            healTB.setText(savedInstanceState.getString("healTB"));
            intimidateTB.setText(savedInstanceState.getString("intimidateTB"));
            arcanaTB.setText(savedInstanceState.getString("arcanaTB"));
            dungeoneeringTB.setText(savedInstanceState.getString("dungeoneeringTB"));
            engineeringTB.setText(savedInstanceState.getString("engineeringTB"));
            geographyTB.setText(savedInstanceState.getString("geographyTB"));
            historyTB.setText(savedInstanceState.getString("historyTB"));
            localTB.setText(savedInstanceState.getString("localTB"));
            natureTB.setText(savedInstanceState.getString("natureTB"));
            nobilityTB.setText(savedInstanceState.getString("nobilityTB"));
            planesTB.setText(savedInstanceState.getString("planesTB"));
            religionTB.setText(savedInstanceState.getString("religionTB"));
            linguisticsTB.setText(savedInstanceState.getString("linguisticsTB"));
            perceptionTB.setText(savedInstanceState.getString("perceptionTB"));
            performTB.setText(savedInstanceState.getString("performTB"));
            professionTB.setText(savedInstanceState.getString("professionTB"));
            rideTB.setText(savedInstanceState.getString("rideTB"));
            senseMotiveTB.setText(savedInstanceState.getString("senseMotiveTB"));
            slightOfHandTB.setText(savedInstanceState.getString("slightOfHandTB"));
            spellcraftTB.setText(savedInstanceState.getString("spellcraftTB"));
            stealthTB.setText(savedInstanceState.getString("stealthTB"));
            survivalTB.setText(savedInstanceState.getString("survivalTB"));
            swimTB.setText(savedInstanceState.getString("swimTB"));

            //ability mod
            acrobaticsAM.setText(savedInstanceState.getString("acrobaticsAM"));
            appraiseAM.setText(savedInstanceState.getString("appraiseAM"));
            bluffAM.setText(savedInstanceState.getString("bluffAM"));
            climbAM.setText(savedInstanceState.getString("climbAM"));
            craftAM.setText(savedInstanceState.getString("craftAM"));
            diplomacyAM.setText(savedInstanceState.getString("diplomacyAM"));
            disableDeviceAM.setText(savedInstanceState.getString("disableDeviceAM"));
            disguiseAM.setText(savedInstanceState.getString("disguiseAM"));
            escapeArtistAM.setText(savedInstanceState.getString("escapeArtistAM"));
            flyAM.setText(savedInstanceState.getString("flyAM"));
            handleAnimalAM.setText(savedInstanceState.getString("handleAnimalAM"));
            healAM.setText(savedInstanceState.getString("healAM"));
            intimidateAM.setText(savedInstanceState.getString("intimidateAM"));
            arcanaAM.setText(savedInstanceState.getString("arcanaAM"));
            dungeoneeringAM.setText(savedInstanceState.getString("dungeoneeringAM"));
            engineeringAM.setText(savedInstanceState.getString("engineeringAM"));
            geographyAM.setText(savedInstanceState.getString("geographyAM"));
            historyAM.setText(savedInstanceState.getString("historyAM"));
            localAM.setText(savedInstanceState.getString("localAM"));
            natureAM.setText(savedInstanceState.getString("natureAM"));
            nobilityAM.setText(savedInstanceState.getString("nobilityAM"));
            planesAM.setText(savedInstanceState.getString("planesAM"));
            religionAM.setText(savedInstanceState.getString("religionAM"));
            linguisticsAM.setText(savedInstanceState.getString("linguisticsAM"));
            perceptionAM.setText(savedInstanceState.getString("perceptionAM"));
            performAM.setText(savedInstanceState.getString("performAM"));
            professionAM.setText(savedInstanceState.getString("professionAM"));
            rideAM.setText(savedInstanceState.getString("rideAM"));
            senseMotiveAM.setText(savedInstanceState.getString("senseMotiveAM"));
            slightOfHandAM.setText(savedInstanceState.getString("slightOfHandAM"));
            spellcraftAM.setText(savedInstanceState.getString("spellcraftAM"));
            stealthAM.setText(savedInstanceState.getString("stealthAM"));
            survivalAM.setText(savedInstanceState.getString("survivalAM"));
            swimAM.setText(savedInstanceState.getString("swimAM"));

            //ranks
            acrobaticsR.setText(savedInstanceState.getString("acrobaticsR"));
            appraiseR.setText(savedInstanceState.getString("appraiseR"));
            bluffR.setText(savedInstanceState.getString("bluffR"));
            climbR.setText(savedInstanceState.getString("climbR"));
            craftR.setText(savedInstanceState.getString("craftR"));
            diplomacyR.setText(savedInstanceState.getString("diplomacyR"));
            disableDeviceR.setText(savedInstanceState.getString("disableDeviceR"));
            disguiseR.setText(savedInstanceState.getString("disguiseR"));
            escapeArtistR.setText(savedInstanceState.getString("escapeArtistR"));
            flyR.setText(savedInstanceState.getString("flyR"));
            handleAnimalR.setText(savedInstanceState.getString("handleAnimalR"));
            healR.setText(savedInstanceState.getString("healR"));
            intimidateR.setText(savedInstanceState.getString("intimidateR"));
            arcanaR.setText(savedInstanceState.getString("arcanaR"));
            dungeoneeringR.setText(savedInstanceState.getString("dungeoneeringR"));
            engineeringR.setText(savedInstanceState.getString("engineeringR"));
            geographyR.setText(savedInstanceState.getString("geographyR"));
            historyR.setText(savedInstanceState.getString("historyR"));
            localR.setText(savedInstanceState.getString("localR"));
            natureR.setText(savedInstanceState.getString("natureR"));
            nobilityR.setText(savedInstanceState.getString("nobilityR"));
            planesR.setText(savedInstanceState.getString("planesR"));
            religionR.setText(savedInstanceState.getString("religionR"));
            linguisticsR.setText(savedInstanceState.getString("linguisticsR"));
            perceptionR.setText(savedInstanceState.getString("perceptionR"));
            performR.setText(savedInstanceState.getString("performR"));
            professionR.setText(savedInstanceState.getString("professionR"));
            rideR.setText(savedInstanceState.getString("rideR"));
            senseMotiveR.setText(savedInstanceState.getString("senseMotiveR"));
            slightOfHandR.setText(savedInstanceState.getString("slightOfHandR"));
            spellcraftR.setText(savedInstanceState.getString("spellcraftR"));
            stealthR.setText(savedInstanceState.getString("stealthR"));
            survivalR.setText(savedInstanceState.getString("survivalR"));
            swimR.setText(savedInstanceState.getString("swimR"));

            //misc mod
            acrobaticsMM.setText(savedInstanceState.getString("acrobaticsMM"));
            appraiseMM.setText(savedInstanceState.getString("appraiseMM"));
            bluffMM.setText(savedInstanceState.getString("bluffMM"));
            climbMM.setText(savedInstanceState.getString("climbMM"));
            craftMM.setText(savedInstanceState.getString("craftMM"));
            diplomacyMM.setText(savedInstanceState.getString("diplomacyMM"));
            disableDeviceMM.setText(savedInstanceState.getString("disableDeviceMM"));
            disguiseMM.setText(savedInstanceState.getString("disguiseMM"));
            escapeArtistMM.setText(savedInstanceState.getString("escapeArtistMM"));
            flyMM.setText(savedInstanceState.getString("flyMM"));
            handleAnimalMM.setText(savedInstanceState.getString("handleAnimalMM"));
            healMM.setText(savedInstanceState.getString("healMM"));
            intimidateMM.setText(savedInstanceState.getString("intimidateMM"));
            arcanaMM.setText(savedInstanceState.getString("arcanaMM"));
            dungeoneeringMM.setText(savedInstanceState.getString("dungeoneeringMM"));
            engineeringMM.setText(savedInstanceState.getString("engineeringMM"));
            geographyMM.setText(savedInstanceState.getString("geographyMM"));
            historyMM.setText(savedInstanceState.getString("historyMM"));
            localMM.setText(savedInstanceState.getString("localMM"));
            natureMM.setText(savedInstanceState.getString("natureMM"));
            nobilityMM.setText(savedInstanceState.getString("nobilityMM"));
            planesMM.setText(savedInstanceState.getString("planesMM"));
            religionMM.setText(savedInstanceState.getString("religionMM"));
            linguisticsMM.setText(savedInstanceState.getString("linguisticsMM"));
            perceptionMM.setText(savedInstanceState.getString("perceptionMM"));
            performMM.setText(savedInstanceState.getString("performMM"));
            professionMM.setText(savedInstanceState.getString("professionMM"));
            rideMM.setText(savedInstanceState.getString("rideMM"));
            senseMotiveMM.setText(savedInstanceState.getString("senseMotiveMM"));
            slightOfHandMM.setText(savedInstanceState.getString("slightOfHandMM"));
            spellcraftMM.setText(savedInstanceState.getString("spellcraftMM"));
            stealthMM.setText(savedInstanceState.getString("stealthMM"));
            survivalMM.setText(savedInstanceState.getString("survivalMM"));
            swimMM.setText(savedInstanceState.getString("swimMM"));


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_class, container, false);
        classSpin = (Spinner)v.findViewById(R.id.classSpinner);

        //checkboxes
        acrobaticsCB = (CheckBox)v.findViewById(R.id.acrobaticsCheck);
        appraiseCB = (CheckBox)v.findViewById(R.id.appraiseCheck);
        bluffCB = (CheckBox)v.findViewById(R.id.bluffCheck);
        climbCB = (CheckBox)v.findViewById(R.id.climbCheck);
        craftCB = (CheckBox)v.findViewById(R.id.craftCheck);
        diplomacyCB = (CheckBox)v.findViewById(R.id.diplomacyCheck);
        disableDeviceCB = (CheckBox)v.findViewById(R.id.disableDeviceCheck);
        disguiseCB = (CheckBox)v.findViewById(R.id.disguiseCheck);
        escapeArtistCB = (CheckBox)v.findViewById(R.id.escapeArtistCheck);
        flyCB = (CheckBox)v.findViewById(R.id.flyCheck);
        handleAnimalCB = (CheckBox)v.findViewById(R.id.handleAnimalCheck);
        healCB = (CheckBox)v.findViewById(R.id.healCheck);
        intimidateCB = (CheckBox)v.findViewById(R.id.intimidateCheck);
        arcanaCB = (CheckBox)v.findViewById(R.id.arcanaCheck);
        dungeoneeringCB = (CheckBox)v.findViewById(R.id.dungeoneeringCheck);
        engineeringCB = (CheckBox)v.findViewById(R.id.engineeringCheck);
        geographyCB = (CheckBox)v.findViewById(R.id.geographyCheck);
        historyCB = (CheckBox)v.findViewById(R.id.historyCheck);
        localCB = (CheckBox)v.findViewById(R.id.localCheck);
        natureCB = (CheckBox)v.findViewById(R.id.natureCheck);
        nobilityCB = (CheckBox)v.findViewById(R.id.nobilityCheck);
        planesCB = (CheckBox)v.findViewById(R.id.planesCheck);
        religionCB = (CheckBox)v.findViewById(R.id.religionCheck);
        linguisticsCB = (CheckBox)v.findViewById(R.id.linguisticsCheck);
        perceptionCB = (CheckBox)v.findViewById(R.id.perceptionCheck);
        performCB = (CheckBox)v.findViewById(R.id.performCheck);
        professionCB = (CheckBox)v.findViewById(R.id.professionCheck);
        rideCB = (CheckBox)v.findViewById(R.id.rideCheck);
        senseMotiveCB = (CheckBox)v.findViewById(R.id.senseMotiveCheck);
        slightOfHandCB = (CheckBox)v.findViewById(R.id.slightOfHandCheck);
        spellcraftCB = (CheckBox)v.findViewById(R.id.spellcraftCheck);
        stealthCB = (CheckBox)v.findViewById(R.id.stealthCheck);
        survivalCB = (CheckBox)v.findViewById(R.id.survivalCheck);
        swimCB = (CheckBox)v.findViewById(R.id.swimCheck);

        //total bonuses
        acrobaticsTB = (EditText)v.findViewById(R.id.acrobaticsTotalBonus);
        appraiseTB = (EditText)v.findViewById(R.id.appraiseTotalBonus);
        bluffTB = (EditText)v.findViewById(R.id.bluffTotalBonus);
        climbTB = (EditText)v.findViewById(R.id.climbTotalBonus);
        craftTB = (EditText)v.findViewById(R.id.craftTotalBonus);
        diplomacyTB = (EditText)v.findViewById(R.id.diplomacyTotalBonus);
        disableDeviceTB = (EditText)v.findViewById(R.id.disableDeviceTotalBonus);
        disguiseTB = (EditText)v.findViewById(R.id.disguiseTotalBonus);
        escapeArtistTB = (EditText)v.findViewById(R.id.escapeArtistTotalBonus);
        flyTB = (EditText)v.findViewById(R.id.flyTotalBonus);
        handleAnimalTB = (EditText)v.findViewById(R.id.handleAnimalTotalBonus);
        healTB = (EditText)v.findViewById(R.id.healTotalBonus);
        intimidateTB = (EditText)v.findViewById(R.id.intimidateTotalBonus);
        arcanaTB = (EditText)v.findViewById(R.id.arcanaTotalBonus);
        dungeoneeringTB = (EditText)v.findViewById(R.id.dungeoneeringTotalBonus);
        engineeringTB = (EditText)v.findViewById(R.id.engineeringTotalBonus);
        geographyTB = (EditText)v.findViewById(R.id.geographyTotalBonus);
        historyTB = (EditText)v.findViewById(R.id.historyTotalBonus);
        localTB = (EditText)v.findViewById(R.id.localTotalBonus);
        natureTB = (EditText)v.findViewById(R.id.natureTotalBonus);
        nobilityTB = (EditText)v.findViewById(R.id.nobilityTotalBonus);
        planesTB = (EditText)v.findViewById(R.id.planesTotalBonus);
        religionTB = (EditText)v.findViewById(R.id.religionTotalBonus);
        linguisticsTB = (EditText)v.findViewById(R.id.linguisticsTotalBonus);
        perceptionTB = (EditText)v.findViewById(R.id.perceptionTotalBonus);
        performTB = (EditText)v.findViewById(R.id.performTotalBonus);
        professionTB = (EditText)v.findViewById(R.id.professionTotalBonus);
        rideTB = (EditText)v.findViewById(R.id.rideTotalBonus);
        senseMotiveTB = (EditText)v.findViewById(R.id.senseMotiveTotalBonus);
        slightOfHandTB = (EditText)v.findViewById(R.id.slightOfHandTotalBonus);
        spellcraftTB = (EditText)v.findViewById(R.id.spellcraftTotalBonus);
        stealthTB = (EditText)v.findViewById(R.id.stealthTotalBonus);
        survivalTB = (EditText)v.findViewById(R.id.survivalTotalBonus);
        swimTB = (EditText)v.findViewById(R.id.swimTotalBonus);

        //ability mod
        acrobaticsAM = (EditText)v.findViewById(R.id.acrobaticsAbilityMod);
        appraiseAM = (EditText)v.findViewById(R.id.appraiseAbilityMod);
        bluffAM = (EditText)v.findViewById(R.id.bluffAbilityMod);
        climbAM = (EditText)v.findViewById(R.id.climbAbilityMod);
        craftAM = (EditText)v.findViewById(R.id.craftAbilityMod);
        diplomacyAM = (EditText)v.findViewById(R.id.diplomacyAbilityMod);
        disableDeviceAM = (EditText)v.findViewById(R.id.disableDeviceAbilityMod);
        disguiseAM = (EditText)v.findViewById(R.id.disguiseAbilityMod);
        escapeArtistAM = (EditText)v.findViewById(R.id.escapeArtistAbilityMod);
        flyAM = (EditText)v.findViewById(R.id.flyAbilityMod);
        handleAnimalAM = (EditText)v.findViewById(R.id.handleAnimalAbilityMod);
        healAM = (EditText)v.findViewById(R.id.healAbilityMod);
        intimidateAM = (EditText)v.findViewById(R.id.intimidateAbilityMod);
        arcanaAM = (EditText)v.findViewById(R.id.arcanaAbilityMod);
        dungeoneeringAM = (EditText)v.findViewById(R.id.dungeoneeringAbilityMod);
        engineeringAM = (EditText)v.findViewById(R.id.engineeringAbilityMod);
        geographyAM = (EditText)v.findViewById(R.id.geographyAbilityMod);
        historyAM = (EditText)v.findViewById(R.id.historyAbilityMod);
        localAM = (EditText)v.findViewById(R.id.localAbilityMod);
        natureAM = (EditText)v.findViewById(R.id.natureAbilityMod);
        nobilityAM = (EditText)v.findViewById(R.id.nobilityAbilityMod);
        planesAM = (EditText)v.findViewById(R.id.planesAbilityMod);
        religionAM = (EditText)v.findViewById(R.id.religionAbilityMod);
        linguisticsAM = (EditText)v.findViewById(R.id.linguisticsAbilityMod);
        perceptionAM = (EditText)v.findViewById(R.id.perceptionAbilityMod);
        performAM = (EditText)v.findViewById(R.id.performAbilityMod);
        professionAM = (EditText)v.findViewById(R.id.professionAbilityMod);
        rideAM = (EditText)v.findViewById(R.id.rideAbilityMod);
        senseMotiveAM = (EditText)v.findViewById(R.id.senseMotiveAbilityMod);
        slightOfHandAM = (EditText)v.findViewById(R.id.slightOfHandAbilityMod);
        spellcraftAM = (EditText)v.findViewById(R.id.spellcraftAbilityMod);
        stealthAM = (EditText)v.findViewById(R.id.stealthAbilityMod);
        survivalAM = (EditText)v.findViewById(R.id.survivalAbilityMod);
        swimAM = (EditText)v.findViewById(R.id.swimAbilityMod);

        //ranks
        acrobaticsR = (EditText)v.findViewById(R.id.acrobaticsRanks);
        appraiseR = (EditText)v.findViewById(R.id.appraiseRanks);
        bluffR = (EditText)v.findViewById(R.id.bluffRanks);
        climbR = (EditText)v.findViewById(R.id.climbRanks);
        craftR = (EditText)v.findViewById(R.id.craftRanks);
        diplomacyR = (EditText)v.findViewById(R.id.diplomacyRanks);
        disableDeviceR = (EditText)v.findViewById(R.id.disableDeviceRanks);
        disguiseR = (EditText)v.findViewById(R.id.disguiseRanks);
        escapeArtistR = (EditText)v.findViewById(R.id.escapeArtistRanks);
        flyR = (EditText)v.findViewById(R.id.flyRanks);
        handleAnimalR = (EditText)v.findViewById(R.id.handleAnimalRanks);
        healR = (EditText)v.findViewById(R.id.healRanks);
        intimidateR = (EditText)v.findViewById(R.id.intimidateRanks);
        arcanaR = (EditText)v.findViewById(R.id.arcanaRanks);
        dungeoneeringR = (EditText)v.findViewById(R.id.dungeoneeringRanks);
        engineeringR = (EditText)v.findViewById(R.id.engineeringRanks);
        geographyR = (EditText)v.findViewById(R.id.geographyRanks);
        historyR = (EditText)v.findViewById(R.id.historyRanks);
        localR = (EditText)v.findViewById(R.id.localRanks);
        natureR = (EditText)v.findViewById(R.id.natureRanks);
        nobilityR = (EditText)v.findViewById(R.id.nobilityRanks);
        planesR = (EditText)v.findViewById(R.id.planesRanks);
        religionR = (EditText)v.findViewById(R.id.religionRanks);
        linguisticsR = (EditText)v.findViewById(R.id.linguisticsRanks);
        perceptionR = (EditText)v.findViewById(R.id.perceptionRanks);
        performR = (EditText)v.findViewById(R.id.performRanks);
        professionR = (EditText)v.findViewById(R.id.professionRanks);
        rideR = (EditText)v.findViewById(R.id.rideRanks);
        senseMotiveR = (EditText)v.findViewById(R.id.senseMotiveRanks);
        slightOfHandR = (EditText)v.findViewById(R.id.slightOfHandRanks);
        spellcraftR = (EditText)v.findViewById(R.id.spellcraftRanks);
        stealthR = (EditText)v.findViewById(R.id.stealthRanks);
        survivalR = (EditText)v.findViewById(R.id.survivalRanks);
        swimR = (EditText)v.findViewById(R.id.swimRanks);

        //misc mod
        acrobaticsMM = (EditText)v.findViewById(R.id.acrobaticsMiscMod);
        appraiseMM = (EditText)v.findViewById(R.id.appraiseMiscMod);
        bluffMM = (EditText)v.findViewById(R.id.bluffMiscMod);
        climbMM = (EditText)v.findViewById(R.id.climbMiscMod);
        craftMM = (EditText)v.findViewById(R.id.craftMiscMod);
        diplomacyMM = (EditText)v.findViewById(R.id.diplomacyMiscMod);
        disableDeviceMM = (EditText)v.findViewById(R.id.disableDeviceMiscMod);
        disguiseMM = (EditText)v.findViewById(R.id.disguiseMiscMod);
        escapeArtistMM = (EditText)v.findViewById(R.id.escapeArtistMiscMod);
        flyMM = (EditText)v.findViewById(R.id.flyMiscMod);
        handleAnimalMM = (EditText)v.findViewById(R.id.handleAnimalMiscMod);
        healMM = (EditText)v.findViewById(R.id.healMiscMod);
        intimidateMM = (EditText)v.findViewById(R.id.intimidateMiscMod);
        arcanaMM = (EditText)v.findViewById(R.id.arcanaMiscMod);
        dungeoneeringMM = (EditText)v.findViewById(R.id.dungeoneeringMiscMod);
        engineeringMM = (EditText)v.findViewById(R.id.engineeringMiscMod);
        geographyMM = (EditText)v.findViewById(R.id.geographyMiscMod);
        historyMM = (EditText)v.findViewById(R.id.historyMiscMod);
        localMM = (EditText)v.findViewById(R.id.localMiscMod);
        natureMM = (EditText)v.findViewById(R.id.natureMiscMod);
        nobilityMM = (EditText)v.findViewById(R.id.nobilityMiscMod);
        planesMM = (EditText)v.findViewById(R.id.planesMiscMod);
        religionMM = (EditText)v.findViewById(R.id.religionMiscMod);
        linguisticsMM = (EditText)v.findViewById(R.id.linguisticsMiscMod);
        perceptionMM = (EditText)v.findViewById(R.id.perceptionMiscMod);
        performMM = (EditText)v.findViewById(R.id.performMiscMod);
        professionMM = (EditText)v.findViewById(R.id.professionMiscMod);
        rideMM = (EditText)v.findViewById(R.id.rideMiscMod);
        senseMotiveMM = (EditText)v.findViewById(R.id.senseMotiveMiscMod);
        slightOfHandMM = (EditText)v.findViewById(R.id.slightOfHandMiscMod);
        spellcraftMM = (EditText)v.findViewById(R.id.spellcraftMiscMod);
        stealthMM = (EditText)v.findViewById(R.id.stealthMiscMod);
        survivalMM = (EditText)v.findViewById(R.id.survivalMiscMod);
        swimMM = (EditText)v.findViewById(R.id.swimMiscMod);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    // Sets all fields to those of the loaded character
    public void onResume()
    {
        super.onResume();
        String[][] skills = new String[34][5];
        Character character = CharacterApplication.getCharacter();
        skills = character.getSkills();
        classSpin.setSelection(character.getCharClass());
        for(int i = 0; i < SKILL_NAMES.length; i++) {
            String checkBoxId = SKILL_NAMES[i] + "Check";
            String rankId = SKILL_NAMES[i] + "Ranks";
            String abilityId = SKILL_NAMES[i] + "AbilityMod";
            String totalId = SKILL_NAMES[i] + "TotalBonus";
            String miscId = SKILL_NAMES[i] + "MiscMod";
            int id = getResources().getIdentifier(checkBoxId, "id", v.getContext().getPackageName());
            CheckBox checkbox = (CheckBox) v.findViewById(id);
            checkbox.setChecked(Boolean.valueOf(skills[i][0]));
            id = getResources().getIdentifier(rankId, "id", v.getContext().getPackageName());
            EditText editText = (EditText) v.findViewById(id);
            editText.setText(skills[i][1]);
            id = getResources().getIdentifier(abilityId, "id", v.getContext().getPackageName());
            editText = (EditText) v.findViewById(id);
            editText.setText(skills[i][2]);
            id = getResources().getIdentifier(totalId, "id", v.getContext().getPackageName());
            editText = (EditText) v.findViewById(id);
            editText.setText(skills[i][3]);
            id = getResources().getIdentifier(miscId, "id", v.getContext().getPackageName());
            editText = (EditText) v.findViewById(id);
            editText.setText(skills[i][4]);
        }
    }
    @Override
    //Saves values of all fields to the character object stored in the Application Object
    public void onPause()
    {
        super.onPause();
        String[][] skills = new String[34][5];

        for(int i = 0; i < SKILL_NAMES.length; i++) {
            String checkBoxId = SKILL_NAMES[i] + "Check";
            String rankId = SKILL_NAMES[i] + "Ranks";
            String abilityId = SKILL_NAMES[i] + "AbilityMod";
            String totalId = SKILL_NAMES[i] + "TotalBonus";
            String miscId = SKILL_NAMES[i] + "MiscMod";
            int id = getResources().getIdentifier(checkBoxId, "id", v.getContext().getPackageName());
            CheckBox checkbox = (CheckBox) v.findViewById(id);
            skills[i][0] = String.valueOf(checkbox.isChecked());
            id = getResources().getIdentifier(rankId, "id", v.getContext().getPackageName());
            EditText editText = (EditText) v.findViewById(id);
            skills[i][1] = editText.getText().toString();
            id = getResources().getIdentifier(abilityId, "id", v.getContext().getPackageName());
            editText = (EditText) v.findViewById(id);
            skills[i][2] = editText.getText().toString();
            id = getResources().getIdentifier(totalId, "id", v.getContext().getPackageName());
            editText = (EditText) v.findViewById(id);
            skills[i][3] = editText.getText().toString();
            id = getResources().getIdentifier(miscId, "id", v.getContext().getPackageName());
            editText = (EditText) v.findViewById(id);
            skills[i][4] = editText.getText().toString();
        }
        Character character = CharacterApplication.getCharacter();
        character.setSkills(skills);
        character.setCharClass(classSpin.getSelectedItemPosition());
        CharacterApplication.setCharacter(character);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);



        outState.putInt("class", classSpin.getSelectedItemPosition());

        //checkboxes
        outState.putBoolean("acrobaticsCB", acrobaticsCB.isChecked());
        outState.putBoolean("appraiseCB", appraiseCB.isChecked());
        outState.putBoolean("bluffCB", bluffCB.isChecked());
        outState.putBoolean("climbCB", climbCB.isChecked());
        outState.putBoolean("craftCB", craftCB.isChecked());
        outState.putBoolean("diplomacyCB", diplomacyCB.isChecked());
        outState.putBoolean("disableDeviceCB", disableDeviceCB.isChecked());
        outState.putBoolean("disguiseCB", disguiseCB.isChecked());
        outState.putBoolean("escapeArtistCB", escapeArtistCB.isChecked());
        outState.putBoolean("flyCB", flyCB.isChecked());
        outState.putBoolean("handleAnimalCB", handleAnimalCB.isChecked());
        outState.putBoolean("healCB", healCB.isChecked());
        outState.putBoolean("intimidateCB", intimidateCB.isChecked());
        outState.putBoolean("arcanaCB", arcanaCB.isChecked());
        outState.putBoolean("dungeoneeringCB", dungeoneeringCB.isChecked());
        outState.putBoolean("engineeringCB", engineeringCB.isChecked());
        outState.putBoolean("geographyCB", geographyCB.isChecked());
        outState.putBoolean("historyCB", historyCB.isChecked());
        outState.putBoolean("localCB", localCB.isChecked());
        outState.putBoolean("natureCB", natureCB.isChecked());
        outState.putBoolean("nobilityCB", nobilityCB.isChecked());
        outState.putBoolean("planesCB", planesCB.isChecked());
        outState.putBoolean("religionCB", religionCB.isChecked());
        outState.putBoolean("linguisticsCB", linguisticsCB.isChecked());
        outState.putBoolean("perceptionCB", perceptionCB.isChecked());
        outState.putBoolean("performCB", performCB.isChecked());
        outState.putBoolean("professionCB", professionCB.isChecked());
        outState.putBoolean("rideCB", rideCB.isChecked());
        outState.putBoolean("senseMotiveCB", senseMotiveCB.isChecked());
        outState.putBoolean("slightOfHandCB", slightOfHandCB.isChecked());
        outState.putBoolean("spellcraftCB", spellcraftCB.isChecked());
        outState.putBoolean("stealthCB", stealthCB.isChecked());
        outState.putBoolean("survivalCB", survivalCB.isChecked());
        outState.putBoolean("swimCB", swimCB.isChecked());

        //total bonus
        outState.putString("acrobaticsTB", acrobaticsTB.getText().toString());
        outState.putString("appraiseTB", appraiseTB.getText().toString());
        outState.putString("bluffTB", bluffTB.getText().toString());
        outState.putString("climbTB", climbTB.getText().toString());
        outState.putString("craftTB", craftTB.getText().toString());
        outState.putString("diplomacyTB", diplomacyTB.getText().toString());
        outState.putString("disableDeviceTB", disableDeviceTB.getText().toString());
        outState.putString("disguiseTB", disguiseTB.getText().toString());
        outState.putString("escapeArtistTB", escapeArtistTB.getText().toString());
        outState.putString("flyTB", flyTB.getText().toString());
        outState.putString("handleAnimalTB", handleAnimalTB.getText().toString());
        outState.putString("healTB", healTB.getText().toString());
        outState.putString("intimidateTB", intimidateTB.getText().toString());
        outState.putString("arcanaTB", arcanaTB.getText().toString());
        outState.putString("dungeoneeringTB", dungeoneeringTB.getText().toString());
        outState.putString("engineeringTB", engineeringTB.getText().toString());
        outState.putString("geographyTB", geographyTB.getText().toString());
        outState.putString("historyTB", historyTB.getText().toString());
        outState.putString("localTB", localTB.getText().toString());
        outState.putString("natureTB", natureTB.getText().toString());
        outState.putString("nobilityTB", nobilityTB.getText().toString());
        outState.putString("planesTB", planesTB.getText().toString());
        outState.putString("religionTB", religionTB.getText().toString());
        outState.putString("linguisticsTB", linguisticsTB.getText().toString());
        outState.putString("perceptionTB", perceptionTB.getText().toString());
        outState.putString("performTB", performTB.getText().toString());
        outState.putString("professionTB", professionTB.getText().toString());
        outState.putString("rideTB", rideTB.getText().toString());
        outState.putString("senseMotiveTB", senseMotiveTB.getText().toString());
        outState.putString("slightOfHandTB", slightOfHandTB.getText().toString());
        outState.putString("spellcraftTB", spellcraftTB.getText().toString());
        outState.putString("stealthTB", stealthTB.getText().toString());
        outState.putString("survivalTB", survivalTB.getText().toString());
        outState.putString("swimTB", swimTB.getText().toString());

        //ability mod
        outState.putString("acrobaticsAM", acrobaticsAM.getText().toString());
        outState.putString("appraiseAM", appraiseAM.getText().toString());
        outState.putString("bluffAM", bluffAM.getText().toString());
        outState.putString("climbAM", climbAM.getText().toString());
        outState.putString("craftAM", craftAM.getText().toString());
        outState.putString("diplomacyAM", diplomacyAM.getText().toString());
        outState.putString("disableDeviceAM", disableDeviceAM.getText().toString());
        outState.putString("disguiseAM", disguiseAM.getText().toString());
        outState.putString("escapeArtistAM", escapeArtistAM.getText().toString());
        outState.putString("flyAM", flyAM.getText().toString());
        outState.putString("handleAnimalAM", handleAnimalAM.getText().toString());
        outState.putString("healAM", healAM.getText().toString());
        outState.putString("intimidateAM", intimidateAM.getText().toString());
        outState.putString("arcanaAM", arcanaAM.getText().toString());
        outState.putString("dungeoneeringAM", dungeoneeringAM.getText().toString());
        outState.putString("engineeringAM", engineeringAM.getText().toString());
        outState.putString("geographyAM", geographyAM.getText().toString());
        outState.putString("historyAM", historyAM.getText().toString());
        outState.putString("localAM", localAM.getText().toString());
        outState.putString("natureAM", natureAM.getText().toString());
        outState.putString("nobilityAM", nobilityAM.getText().toString());
        outState.putString("planesAM", planesAM.getText().toString());
        outState.putString("religionAM", religionAM.getText().toString());
        outState.putString("linguisticsAM", linguisticsAM.getText().toString());
        outState.putString("perceptionAM", perceptionAM.getText().toString());
        outState.putString("performAM", performAM.getText().toString());
        outState.putString("professionAM", professionAM.getText().toString());
        outState.putString("rideAM", rideAM.getText().toString());
        outState.putString("senseMotiveAM", senseMotiveAM.getText().toString());
        outState.putString("slightOfHandAM", slightOfHandAM.getText().toString());
        outState.putString("spellcraftAM", spellcraftAM.getText().toString());
        outState.putString("stealthAM", stealthAM.getText().toString());
        outState.putString("survivalAM", survivalAM.getText().toString());
        outState.putString("swimAM", swimAM.getText().toString());

        //RANKS
        outState.putString("acrobaticsR", acrobaticsR.getText().toString());
        outState.putString("appraiseR", appraiseR.getText().toString());
        outState.putString("bluffR", bluffR.getText().toString());
        outState.putString("climbR", climbR.getText().toString());
        outState.putString("craftR", craftR.getText().toString());
        outState.putString("diplomacyR", diplomacyR.getText().toString());
        outState.putString("disableDeviceR", disableDeviceR.getText().toString());
        outState.putString("disguiseR", disguiseR.getText().toString());
        outState.putString("escapeArtistR", escapeArtistR.getText().toString());
        outState.putString("flyR", flyR.getText().toString());
        outState.putString("handleAnimalR", handleAnimalR.getText().toString());
        outState.putString("healR", healR.getText().toString());
        outState.putString("intimidateR", intimidateR.getText().toString());
        outState.putString("arcanaR", arcanaR.getText().toString());
        outState.putString("dungeoneeringR", dungeoneeringR.getText().toString());
        outState.putString("engineeringR", engineeringR.getText().toString());
        outState.putString("geographyR", geographyR.getText().toString());
        outState.putString("historyR", historyR.getText().toString());
        outState.putString("localR", localR.getText().toString());
        outState.putString("natureR", natureR.getText().toString());
        outState.putString("nobilityR", nobilityR.getText().toString());
        outState.putString("planesR", planesR.getText().toString());
        outState.putString("religionR", religionR.getText().toString());
        outState.putString("linguisticsR", linguisticsR.getText().toString());
        outState.putString("perceptionR", perceptionR.getText().toString());
        outState.putString("performR", performR.getText().toString());
        outState.putString("professionR", professionR.getText().toString());
        outState.putString("rideR", rideR.getText().toString());
        outState.putString("senseMotiveR", senseMotiveR.getText().toString());
        outState.putString("slightOfHandR", slightOfHandR.getText().toString());
        outState.putString("spellcraftR", spellcraftR.getText().toString());
        outState.putString("stealthR", stealthR.getText().toString());
        outState.putString("survivalR", survivalR.getText().toString());
        outState.putString("swimR", swimR.getText().toString());

        //Misc Mod
        outState.putString("acrobaticsMM", acrobaticsMM.getText().toString());
        outState.putString("appraiseMM", appraiseMM.getText().toString());
        outState.putString("bluffMM", bluffMM.getText().toString());
        outState.putString("climbMM", climbMM.getText().toString());
        outState.putString("craftMM", craftMM.getText().toString());
        outState.putString("diplomacyMM", diplomacyMM.getText().toString());
        outState.putString("disableDeviceMM", disableDeviceMM.getText().toString());
        outState.putString("disguiseMM", disguiseMM.getText().toString());
        outState.putString("escapeArtistMM", escapeArtistMM.getText().toString());
        outState.putString("flyMM", flyMM.getText().toString());
        outState.putString("handleAnimalMM", handleAnimalMM.getText().toString());
        outState.putString("healMM", healMM.getText().toString());
        outState.putString("intimidateMM", intimidateMM.getText().toString());
        outState.putString("arcanaMM", arcanaMM.getText().toString());
        outState.putString("dungeoneeringMM", dungeoneeringMM.getText().toString());
        outState.putString("engineeringMM", engineeringMM.getText().toString());
        outState.putString("geographyMM", geographyMM.getText().toString());
        outState.putString("historyMM", historyMM.getText().toString());
        outState.putString("localMM", localMM.getText().toString());
        outState.putString("natureMM", natureMM.getText().toString());
        outState.putString("nobilityMM", nobilityMM.getText().toString());
        outState.putString("planesMM", planesMM.getText().toString());
        outState.putString("religionMM", religionMM.getText().toString());
        outState.putString("linguisticsMM", linguisticsMM.getText().toString());
        outState.putString("perceptionMM", perceptionMM.getText().toString());
        outState.putString("performMM", performMM.getText().toString());
        outState.putString("professionMM", professionMM.getText().toString());
        outState.putString("rideMM", rideMM.getText().toString());
        outState.putString("senseMotiveMM", senseMotiveMM.getText().toString());
        outState.putString("slightOfHandMM", slightOfHandMM.getText().toString());
        outState.putString("spellcraftMM", spellcraftMM.getText().toString());
        outState.putString("stealthMM", stealthMM.getText().toString());
        outState.putString("survivalMM", survivalMM.getText().toString());
        outState.putString("swimMM", swimMM.getText().toString());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
