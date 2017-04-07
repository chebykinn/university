package scene;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SceneTest {
    Scene scene;
    Action[] fordActions, firstBodyActions;

    @Before
    public void setUp() throws Exception {
        scene = new Scene();
        fordActions = new Action[scene.ACTIONS_NUM];
        fordActions[0] = new WalkTowardsAction();
        fordActions[1] = new ReachAction();
        fordActions[2] = new FootDownAction();
        firstBodyActions = new Action[scene.ACTIONS_NUM];
        firstBodyActions[0] = new LayAction();
        firstBodyActions[1] = new LayAction();
        firstBodyActions[2] = new HoldAction();
    }

    @Test
    public void initial() throws Exception {
        assertEquals(scene.ford.getName(), PersonName.FORD);
        assertEquals(scene.firstBody.getName(), PersonName.FIRST_BODY);
        assertEquals(scene.ford.getActions().size(), scene.ACTIONS_NUM);
        assertEquals(scene.firstBody.getActions().size(), scene.ACTIONS_NUM);
    }

    @Test
    public void run() throws Exception {
        int i = 0;
        while(scene.nextState()) {
            assertEquals(scene.ford.getState(), fordActions[i]);
            assertEquals(scene.firstBody.getState(), firstBodyActions[i]);
            i++;
        }

    }
}