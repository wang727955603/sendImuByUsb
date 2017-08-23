package presenter;

import sensor.data.MessageToPC;

/**
 * @author Created by CM-WANGMIN on 2017/8/21.
 */

public interface viewImpl {
    void showState(String state);
    void showMessage(String messageToPC);
}
