package com.foundit.apk.controller;

import com.foundit.apk.view.IndexFrame;
import java.awt.event.ActionListener;

/**
 * Controller untuk IndexFrame.
 */
public class IndexController {
    private final IndexFrame view;

    public IndexController() {
        this(new IndexFrame(false));
    }

    public IndexController(IndexFrame view) {
        this.view = view;
        bind();
    }

    public void bind() {
        ActionListener openLoginAction = e -> {
            view.dispose();
            new LoginController().init();
        };
        view.addLoginListener(openLoginAction);
        view.addStartListener(openLoginAction);
    }

    public void init() {
        view.setVisible(true);
    }

    public IndexFrame getView() {
        return view;
    }
}
