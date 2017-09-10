package storyboard.services;

import storyboard.domain.Action;

/**
 * Created by zachjustice on 7/30/17.
 */
public interface ActionService {
    Action findActionById(int id);

    Action saveAction(Action action);
}
