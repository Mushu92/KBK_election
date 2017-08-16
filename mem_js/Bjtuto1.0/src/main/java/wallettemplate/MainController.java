/*
 * Copyright by the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wallettemplate;

import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.core.Coin;
import org.bitcoinj.utils.MonetaryFormat;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.fxmisc.easybind.EasyBind;
import wallettemplate.controls.ClickableBitcoinAddress;
import wallettemplate.controls.NotificationBarPane;
import wallettemplate.utils.BitcoinUIModel;
import wallettemplate.utils.easing.EasingMode;
import wallettemplate.utils.easing.ElasticInterpolator;

import static wallettemplate.Main.bitcoin;

/**
 * Gets created auto-magically by FXMLLoader via reflection. The widget fields are set to the GUI controls they're named
 * after. This class handles all the updates and event handling for the main UI.
 */
// ���÷����� ���� FXMLLoader�� ���� �ڵ� �����˴ϴ�. ���� �ʵ�� ���߿� ��� �� GUI��Ʈ�ѷ� �����˴ϴ�. �� Ŭ������ �⺻ UI�� ���� ��� ������Ʈ �� �̺�Ʈ�� ó���մϴ�
public class MainController {
    public HBox controlsBox;
    public Label balance;
    public Button sendMoneyOutBtn;
    public ClickableBitcoinAddress addressControl; //control�� ���� ����

    private BitcoinUIModel model = new BitcoinUIModel();//util�� ���� ����
    private NotificationBarPane.Item syncItem;

    // Called by FXMLLoader.
    public void initialize() {
        addressControl.setOpacity(0.0);
    }

    public void onBitcoinSetup() {
        model.setWallet(bitcoin.wallet());
        //address �����ִ� ��
        //model.addressProperty()���� �Էµ� ������ addressControl�� �״�� �ԷµǴ°�
        addressControl.addressProperty().bind(model.addressProperty());
        //��� ������ ���� �����ִ� ��
        balance.textProperty().bind(EasyBind.map(model.balanceProperty(), coin -> MonetaryFormat.BTC.noCode().format(coin).toString()));
        // Don't let the user click send money when the wallet is empty.
        // ������ ��� �������� ����ڰ� send money �� �� ����.
        //sendMoneyoutbtn�� ��Ȱ��ȭ �Ѵ� -> model.balanceProperty()�� zero�� ���� ��
        sendMoneyOutBtn.disableProperty().bind(model.balanceProperty().isEqualTo(Coin.ZERO));

        showBitcoinSyncMessage();
        model.syncProgressProperty().addListener(x -> {
            if (model.syncProgressProperty().get() >= 1.0) {
                readyToGoAnimation();
                if (syncItem != null) {
                    syncItem.cancel();
                    syncItem = null;
                }
            } else if (syncItem == null) {
                showBitcoinSyncMessage();
            }
        });
    }

    private void showBitcoinSyncMessage() {//���۽ÿ� ������ ��!!
        syncItem = Main.instance.notificationBar.pushItem("Synchronising with the Bitcoin network", model.syncProgressProperty());
    }

    public void sendMoneyOut(ActionEvent event) {//sendmoneyout ��ư
        // Hide this UI and show the send money UI. This UI won't be clickable until the user dismisses send_money.
    	// �� UI�� ����� send_money ui�� �����, �� ui�� send money ui�� ���� �������� �ٽ� Ŭ���� �� ����
        Main.instance.overlayUI("send_money.fxml");//overlay�� �˾�â ó�� �߰��ϴ� ���ε�
    }

    public void settingsClicked(ActionEvent event) {//setting click �̺�Ʈ ó��
        Main.OverlayUI<WalletSettingsController> screen = Main.instance.overlayUI("wallet_settings.fxml");//wallet setting �ҷ�����
        screen.controller.initialize(null);
    }
    public void accessClicked(ActionEvent event) throws Exception {//access click �̺�Ʈ ó��
        Main.OverlayUI<WalletTestController> screen = Main.instance.overlayUI("wallet_test.fxml");//wallet setting �ҷ�����
        screen.controller.initialize();
    }

    public void restoreFromSeedAnimation() {
        // Buttons slide out ...
    	// ��ư�� �������?
        TranslateTransition leave = new TranslateTransition(Duration.millis(1200), controlsBox);
        leave.setByY(80.0);
        leave.play();
    }

    public void readyToGoAnimation() {
        // Buttons slide in and clickable address appears simultaneously.
        TranslateTransition arrive = new TranslateTransition(Duration.millis(1200), controlsBox);
        arrive.setInterpolator(new ElasticInterpolator(EasingMode.EASE_OUT, 1, 2));
        arrive.setToY(0.0);
        FadeTransition reveal = new FadeTransition(Duration.millis(1200), addressControl);
        reveal.setToValue(1.0);
        ParallelTransition group = new ParallelTransition(arrive, reveal);
        group.setDelay(NotificationBarPane.ANIM_OUT_DURATION);
        group.setCycleCount(1);
        group.play();
    }

    public DownloadProgressTracker progressBarUpdater() {
        return model.getDownloadProgressTracker();
    }
}
