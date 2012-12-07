package org.jboss.as.console.client.standalone;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.runtime.ext.Extension;
import org.jboss.as.console.client.shared.runtime.ext.ExtensionView;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;


/**
 * @author Heiko Braun
 * @date 6/7/11
 */
public class StandaloneServerView extends DisposableViewImpl implements StandaloneServerPresenter.MyView {

    private StandaloneServerPresenter presenter;
    private Label headline;
    private DeckPanel reloadPanel;
    private Form<StandaloneServer> form;
    private ExtensionView extensions;

    @Override
    public Widget createWidget() {

        ToolButton reloadBtn = new ToolButton(Console.CONSTANTS.common_label_reload(), new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                Feedback.confirm(Console.CONSTANTS.server_reload_title(),
                        Console.MESSAGES.server_reload_confirm(form.getEditedEntity().getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onReloadServerConfig();
                                }
                            }
                        });
            }
        });
        reloadBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_reload_standaloneServerView());

        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(reloadBtn);

        headline = new Label("HEADLINE");

        headline.setStyleName("content-header-label");

        form = new Form<StandaloneServer>(StandaloneServer.class);
        form.setNumColumns(2);

        TextItem codename = new TextItem("releaseCodename", "Code Name");
        TextItem version = new TextItem("releaseVersion", "Release version");
        TextItem state = new TextItem("serverState", "Server State");

        form.setFields(codename, version, state);

        // ----

        reloadPanel = new DeckPanel();
        reloadPanel.setStyleName("fill-layout-width");

        // ----

        VerticalPanel configUptodate = new VerticalPanel();
        HorizontalPanel uptodateContent = new HorizontalPanel();
        uptodateContent.setStyleName("status-panel");
        uptodateContent.addStyleName("serverUptoDate");

        Image img = new Image(Icons.INSTANCE.status_good());
        HTML desc = new HTML(Console.CONSTANTS.server_config_uptodate());
        uptodateContent.add(desc);
        uptodateContent.add(img);

        //img.getElement().getParentElement().addClassName("InfoBlock");
        img.getElement().getParentElement().setAttribute("style", "padding:15px;vertical-align:middle;width:20%");
        desc.getElement().getParentElement().setAttribute("style", "padding:15px;vertical-align:middle");

        configUptodate.add(uptodateContent);

        // --

        VerticalPanel configNeedsUpdate = new VerticalPanel();
        configNeedsUpdate.add(tools.asWidget());

        HorizontalPanel staleContent = new HorizontalPanel();
        staleContent.setStyleName("status-panel");
        staleContent.addStyleName("serverNeedsUpdate");

        Image img2 = new Image(Icons.INSTANCE.status_warn());
        HTML desc2 = new HTML(Console.CONSTANTS.server_reload_desc());
        staleContent.add(desc2);
        staleContent.add(img2);

        //img2.getElement().getParentElement().addClassName("WarnBlock");
        img2.getElement().getParentElement().setAttribute("style", "padding:15px;vertical-align:middle;width:20%");
        desc2.getElement().getParentElement().setAttribute("style", "padding:15px;vertical-align:middle");

        configNeedsUpdate.add(staleContent);

        reloadPanel.add(configUptodate);
        reloadPanel.add(configNeedsUpdate);
        reloadPanel.showWidget(0);

        OneToOneLayout layout = new OneToOneLayout()
                .setTitle("Standalone Server")
                .setPlain(true)
                .setHeadlineWidget(headline)
                .setDescription(Console.CONSTANTS.server_config_desc())
                .setMaster("Configuration", form.asWidget())
                .addDetail("Status", reloadPanel);


        // ---------------------

        extensions = new ExtensionView();

        // ---------------------
        DefaultTabLayoutPanel tabLayoutpanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");


        tabLayoutpanel.add(layout.build(), "Server", true);
        tabLayoutpanel.add(extensions.asWidget(), "Extensions", true);

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;

    }

    @Override
    public void setPresenter(StandaloneServerPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(StandaloneServer server) {
        form.edit(server);
        headline.setText("Server: "+ server.getName());
    }

    @Override
    public void setReloadRequired(boolean reloadRequired) {
        reloadPanel.showWidget( reloadRequired ? 1:0);
    }

    @Override
    public void setExtensions(List<Extension> result) {
        extensions.setExtensions(result);
    }
}
