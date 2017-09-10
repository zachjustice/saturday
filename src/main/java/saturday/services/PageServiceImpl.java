package storyboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import storyboard.domain.Page;
import storyboard.repositories.PageRepository;

/**
 * Created by zachjustice on 7/29/17.
 */
@Service("pageService")
public class PageServiceImpl implements PageService {
    @Autowired
    private PageRepository pageRepository;

    @Override
    public Page findPageById(int id) {
        return pageRepository.findById(id);
    }

    @Override
    public Page savePage(Page page) {
        return pageRepository.save(page);
    }

}
