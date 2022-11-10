package atnum.content.swing.util;

import atnum.content.swing.components.SettingsTab;
import atnum.content.swing.components.VerticalTab;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;

import static java.util.Objects.nonNull;

public class TabLabelTransformer {

	private TabLabelTransformer() {
	}

	public static JLabel transformTabLabel(SettingsTab tab, int tabPlacement,
                                           String paneName) {
		final Dimension size = tab.getSize();

		final JLabel tabLabel;

		if (paneName == null && (tabPlacement == SwingConstants.LEFT
				|| tabPlacement == SwingConstants.RIGHT)) {
			tabLabel = VerticalTab.fromText(tab.getText(), tabPlacement, tab.getIcon());
		}
		else {
			tabLabel = new JLabel(tab.getText(), tab.getIcon(), SwingConstants.LEFT);
		}

		tabLabel.setName(tab.getName());

		if (nonNull(size)) {
			double scale = UIScale.getUserScaleFactor();

			size.setSize(size.width * scale, size.height * scale);

			tabLabel.setMinimumSize(size);
			tabLabel.setPreferredSize(size);
		}

		return tabLabel;
	}
}
