package storyboard.services;

import storyboard.domain.Page;

/**
 * Created by zachjustice on 7/29/17.
 */
public interface PageService {
    Page findPageById(int id);

    Page savePage(Page author);
}
