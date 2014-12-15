package originate.com.recursivelayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MyActivity extends Activity {

    final static int MAX_ROOT_NODES = 100;

    static Node nodeSet1;
    static Node nodeSet2;
    static Node[] nodeList = new Node[MAX_ROOT_NODES];
    static {
        // Populate Set 1
        nodeSet1 =
                new Node("Level 1",
                        new Node("Level 2",
                                new Node("Level 3",
                                        new Node("Level 4", null))),
                        new Node("Level 2",
                                new Node("Level 3", null)
                        )
                );

        // Populate Set 2
        nodeSet2 =
                new Node("Level 1",
                        new Node("Level 2",
                                new Node("Level 3",
                                        new Node("Level 4",
                                                new Node("Level 5",
                                                        new Node("Level 6",
                                                                new Node("Level 7")
                                                        )
                                                )
                                        )
                                )
                        )
                );

        // Populate a longer example with multiple nodes at the root.
        for (int i = 0; i < MAX_ROOT_NODES; i++) {
            nodeList[i] = i % 2 == 0 ? nodeSet1 : nodeSet2;
        }
    }

    /**
     * Click Listener for collapsing and expanding elements.
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Update the visibility of children
            View children = ((View) v.getParent()).findViewById(R.id.childrenLayout);
            children.setVisibility(children.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);

            // Update the TextView with an indicator.
            TextView tv = ((TextView) v);
            tv.setText((children.getVisibility() == View.GONE ? "+ ": "– ")
                        + tv.getText().subSequence(2, tv.getText().length()).toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        LinearLayout root = (LinearLayout) findViewById(R.id.main_container);
        LayoutInflater inflator = getLayoutInflater();

        // Multiple Root:
        for (Node rootNode : nodeList)
            root.addView(createRecursiveView(inflator, root, rootNode));

        // Single Root:
        // root.addView(createRecursiveView(inflator, root, nodeSet1));

    }

    /**
     * This method displays the value of the Node parameter, and then adds the node's children
     * to the child container view in a recursive manner.
     *
     * @param inflator
     * @param container
     * @param node Recursive data structure that we are trying to model.
     * @return View that needs to be added to a parent container
     */
    private View createRecursiveView(LayoutInflater inflator, ViewGroup container, Node node) {
        View view = inflator.inflate(R.layout.child_layout, container, false);
        // Get a reference to the view subviews
        TextView textView = (TextView) view.findViewById(R.id.childName);
        LinearLayout childContainer = (LinearLayout) view.findViewById(R.id.childrenLayout);

        // Set the contents of the view
        textView.setText("– "+ node.name);
        textView.setOnClickListener(clickListener);

        // Add the children to the childrenContainer
        if (node.children != null) {
            for (Node child : node.children) {
                childContainer.addView(createRecursiveView(inflator, childContainer, child));
            }
        }

        return view;
    }
}
