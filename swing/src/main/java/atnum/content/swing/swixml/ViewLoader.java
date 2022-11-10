/*
 * Copyright (C) 2020 TU Darmstadt, Department of Computer Science,
 * Embedded Systems and Applications Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package atnum.content.swing.swixml;

import static java.util.Objects.nonNull;

import java.awt.Container;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.table.TableColumn;

import atnum.content.swing.components.*;
import atnum.content.swing.swixml.converter.AdaptiveTabTypeConverter;
import atnum.content.swing.swixml.converter.IconConverter;
import atnum.content.swing.swixml.factory.AbstractButtonFactory;
import atnum.content.swing.swixml.factory.AbstractInjectButtonFactory;
import atnum.content.swing.swixml.factory.InjectViewFactory;
import atnum.content.swing.swixml.factory.LabelFactory;
import atnum.content.swing.swixml.processor.*;
import net.atlanticbb.tantlinger.shef.HTMLEditorPane;

import atnum.content.core.inject.Injector;
import atnum.content.swing.components.previews.ArrowToolPreview;
import atnum.content.swing.components.previews.EllipseToolPreview;
import atnum.content.swing.components.previews.LineToolPreview;
import atnum.content.swing.components.previews.PenToolPreview;
import atnum.content.swing.components.previews.PointerToolPreview;
import atnum.content.swing.components.previews.RectangleToolPreview;
import atnum.content.swing.model.AdaptiveTabType;
import atnum.content.swing.table.ButtonEditor;
import atnum.content.swing.table.ButtonRenderer;

import org.swixml.ConverterLibrary;
import org.swixml.Localizer;
import org.swixml.SwingEngine;
import org.swixml.SwingTagLibrary;
import org.swixml.TagLibrary;
import org.swixml.factory.BeanFactory;

public class ViewLoader<T extends Container> extends SwingEngine<T> {

	static {
		SwingEngine.setMacOSXSuport(false);

		ConverterLibrary converterLibrary = ConverterLibrary.getInstance();
		converterLibrary.register(Icon.class, new IconConverter());
		converterLibrary.register(ImageIcon.class, new IconConverter());
		converterLibrary.register(AdaptiveTabType.class, new AdaptiveTabTypeConverter());

		TagLibrary tagLibrary = SwingTagLibrary.getInstance();
		tagLibrary.registerTag("ArrowToolPreview", ArrowToolPreview.class);
		tagLibrary.registerTag("ColorChooserButton", ColorChooserButton.class);
		tagLibrary.registerTag("ComboBox", new BeanFactory(JComboBox.class, new ComboBoxProcessor()));
		tagLibrary.registerTag("DisplayPanel", DisplayPanel.class);
		tagLibrary.registerTag("DocumentPreview", DocumentPreview.class);
		tagLibrary.registerTag("EllipseToolPreview", EllipseToolPreview.class);
		tagLibrary.registerTag("LevelMeter", LevelMeter.class);
		tagLibrary.registerTag("LineToolPreview", LineToolPreview.class);
		tagLibrary.registerTag("Panel", new BeanFactory(JPanel.class, new PanelProcessor()));
		tagLibrary.registerTag("PenToolPreview", PenToolPreview.class);
		tagLibrary.registerTag("PointerToolPreview", PointerToolPreview.class);
		tagLibrary.registerTag("RecordButton", RecordButton.class);
		tagLibrary.registerTag("RectangleToolPreview", RectangleToolPreview.class);
		tagLibrary.registerTag("SlideView", SlideView.class);
		tagLibrary.registerTag("Tab", new BeanFactory(SettingsTab.class, new TabProcessor()));
		tagLibrary.registerTag("AdaptiveTabbedPane", new BeanFactory(AdaptiveTabbedPane.class, new AdaptiveTabbedPaneProcessor()));
		tagLibrary.registerTag("TabbedPane", new BeanFactory(JTabbedPane.class, new TabbedPaneProcessor()));
		tagLibrary.registerTag("Table", new BeanFactory(JTable.class, new TableProcessor()));
		tagLibrary.registerTag("TableColumn", new BeanFactory(TableColumn.class, new TableColumnProcessor()));
		tagLibrary.registerTag("TableButtonEditor", ButtonEditor.class);
		tagLibrary.registerTag("TableButtonRenderer", ButtonRenderer.class);
		tagLibrary.registerTag("TextField", new BeanFactory(JTextField.class, new TextFieldProcessor()));
		tagLibrary.registerTag("TitledSeparator", TitledSeparator.class);
		tagLibrary.registerTag("ToggleComboButton", ToggleComboButton.class);
		tagLibrary.registerTag("ToolGroupButton", ToolGroupButton.class);
		tagLibrary.registerTag("Tree", new BeanFactory(JTree.class, new TreeProcessor()));
	}

	private final Localizer localizer;


	public ViewLoader(T client) {
		this(client, null);
	}

	public ViewLoader(T client, ResourceBundle resourceBundle) {
		this(client, resourceBundle, null);
	}

	public ViewLoader(T client, ResourceBundle resourceBundle, Injector injector) {
		super(client);

		if (nonNull(resourceBundle)) {
			localizer = new ViewLocalizer(resourceBundle);
		}
		else {
			localizer = null;
		}

		TagLibrary tagLibrary = SwingTagLibrary.getInstance();
		tagLibrary.registerTag("Label", new LabelFactory(JLabel.class));
		tagLibrary.registerTag("Button", new AbstractButtonFactory(JButton.class));
		tagLibrary.registerTag("DocumentPreview", new InjectViewFactory(DocumentPreview.class, injector));
		tagLibrary.registerTag("ToggleButton", new AbstractButtonFactory(JToggleButton.class));
		tagLibrary.registerTag("RadioButton", new AbstractButtonFactory(JRadioButton.class));
		tagLibrary.registerTag("RecordButton", new AbstractButtonFactory(RecordButton.class));
		tagLibrary.registerTag("HTMLEditor", new InjectViewFactory(HTMLEditorPane.class, injector));
		tagLibrary.registerTag("FontPickerButton", new AbstractInjectButtonFactory(FontPickerButton.class, injector));
		tagLibrary.registerTag("TeXFontPickerButton", new AbstractInjectButtonFactory(TeXFontPickerButton.class, injector));
		tagLibrary.registerTag("ToolColorPickerButton", new AbstractInjectButtonFactory(ToolColorPickerButton.class, injector));
	}

	@Override
	public Localizer getLocalizer() {
		return nonNull(localizer) ? localizer : super.getLocalizer();
	}

	@Override
	public void setLocale(Locale locale) {
		getLocalizer().setLocale(locale);
	}

	@Override
	public void setResourceBundle(String bundlename) {
		getLocalizer().setResourceBundle(bundlename);
	}

	@Override
	public void setClassLoader(ClassLoader cl) {
		this.cl = cl;
		getLocalizer().setClassLoader(cl);
	}
}
