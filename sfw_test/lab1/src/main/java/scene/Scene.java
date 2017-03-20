package scene;

import java.util.Stack;

public class Scene {
    final static int ACTIONS_NUM = 3;
    Person ford, firstBody;

    public void reset(){
        Stack<Action> fordActions = new Stack<>();
        fordActions.push(new FootDownAction());
        fordActions.push(new ReachAction());
        fordActions.push(new WalkTowardsAction());
        ford = new Person(PersonName.FORD, fordActions);

        Stack<Action> bodyActions = new Stack<>();
        bodyActions.push(new HoldAction());
        bodyActions.push(new LayAction());
        bodyActions.push(new LayAction());
        firstBody = new Person(PersonName.FIRST_BODY, bodyActions);
    }

    public Scene(){
        reset();
    }

    public boolean nextState(){
        boolean fortNext = ford.doAction();
        boolean bodyNext = firstBody.doAction();
        return !fortNext || !bodyNext;
    }
}
