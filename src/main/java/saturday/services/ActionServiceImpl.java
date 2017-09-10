package storyboard.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import storyboard.domain.Action;
import storyboard.repositories.ActionRepository;

@Service("actionService")
public class ActionServiceImpl implements ActionService {

    @Autowired
    ActionRepository actionRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Action findActionById(int id) {
        return actionRepository.findActionById(id);
    }

    @Override
    public Action saveAction(Action action) {
        action = actionRepository.save(action);
        logger.info("ACTION SERVICE IMPL: " + action.toString());
        return action;
    }
}


