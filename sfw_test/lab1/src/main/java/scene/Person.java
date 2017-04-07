package scene;

import java.util.Stack;

public class Person {
    public Person(PersonName name, Stack<Action> actions) {
        this.name = name;
        this.actions = actions;
    }

    public Stack<Action> getActions() {
        return actions;
    }

    public void setActions(Stack<Action> actions) {
        this.actions = actions;
    }

    private Stack<Action> actions;

    public Action getState() {
        return state;
    }

    private Action state;

    public PersonName getName() {
        return name;
    }

    PersonName name;

    public boolean doAction(){
        if( actions.size() > 0 ) {
            Action newAction = actions.pop();
            state = newAction;
            return true;
        }
        return false;
    }
}
