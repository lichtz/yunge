package com.yunge.im.interfaces;

import com.yunge.im.mode.UserBean;

public interface ILoginResult {
    void result(UserBean userBean,String errorTip);
}
