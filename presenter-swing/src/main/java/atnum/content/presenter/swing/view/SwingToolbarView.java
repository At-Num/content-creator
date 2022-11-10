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

package atnum.content.presenter.swing.view;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import atnum.content.core.ExecutableState;
import atnum.content.core.controller.ToolController;
import atnum.content.core.graphics.Color;
import atnum.content.core.model.Document;
import atnum.content.core.model.Page;
import atnum.content.core.text.Font;
import atnum.content.core.text.TeXFont;
import atnum.content.core.tool.ColorPalette;
import atnum.content.core.tool.PaintSettings;
import atnum.content.core.tool.ToolType;
import atnum.content.core.view.Action;
import atnum.content.core.view.ConsumerAction;
import atnum.content.core.view.PresentationParameter;
import atnum.content.presenter.api.presenter.ToolbarPresenter;
import atnum.content.presenter.api.view.ToolbarView;
import atnum.content.swing.components.FontPickerButton;
import atnum.content.swing.components.RecordButton;
import atnum.content.swing.components.ToolColorPickerButton;
import atnum.content.swing.components.ToolGroupButton;
import atnum.content.swing.components.toolbar.CustomizedToolbar;
import atnum.content.swing.converter.ColorConverter;
import atnum.content.swing.converter.FontConverter;
import atnum.content.swing.layout.WrapFlowLayout;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;
import atnum.content.swing.view.ViewPostConstruct;

@SwingView(name = "main-toolbar", presenter = ToolbarPresenter.class)
public class SwingToolbarView extends JPanel implements ToolbarView {

	private final ResourceBundle resourceBundle;

	private final ToolController toolController;

	private ConsumerAction<Color> paletteColorAction;

	private ConsumerAction<Font> textBoxFontAction;

	private ConsumerAction<TeXFont> texBoxFontAction;

	private ButtonGroup colorGroup;

	private ButtonGroup toolGroup;

	private JButton undoButton;

	private JButton redoButton;

	private JButton prevSlideButton;

	private JButton nextSlideButton;

	private ToolColorPickerButton customColorButton;

	private JToggleButton colorButton1;

	private JToggleButton colorButton2;

	private JToggleButton colorButton3;

	private JToggleButton colorButton4;

	private JToggleButton colorButton5;

	private JToggleButton colorButton6;

	private JToggleButton penButton;

	private JToggleButton highlighterButton;

	private JToggleButton pointerButton;

	private JToggleButton textSelectButton;

	private JToggleButton lineButton;

	private JToggleButton arrowButton;

	private JToggleButton rectangleButton;

	private JToggleButton ellipseButton;

	private JToggleButton eraseButton;

	private FontPickerButton textButton;


	private JButton clearButton;

	private JToggleButton gridButton;

	private JToggleButton extendButton;

	private JToggleButton whiteboardButton;

	private JToggleButton displaysButton;

	private JToggleButton zoomInButton;

	private JToggleButton panButton;

	private JButton zoomOutButton;

	private RecordButton startRecordingButton;

	private JButton stopRecordingButton;


	private CustomizedToolbar customizedToolbar;


	@Inject
	SwingToolbarView(ResourceBundle resourceBundle, ToolController toolController) {
		super();

		this.resourceBundle = resourceBundle;
		this.toolController = toolController;

		setLayout(new WrapFlowLayout(FlowLayout.LEFT, 0, 0));
	}

	@Override
	public void setDocument(Document doc) {
		boolean isWhiteboard = nonNull(doc) && doc.isWhiteboard();

		textSelectButton.setEnabled(!isWhiteboard);
		whiteboardButton.setSelected(isWhiteboard);
	}

	@Override
	public void setPage(Page page, PresentationParameter parameter) {
		boolean hasUndo = false;
		boolean hasRedo = false;
		boolean extended = false;
		boolean hasGrid = false;
		boolean zoomedIn = false;

		if (nonNull(page)) {
			hasUndo = page.hasUndoActions();
			hasRedo = page.hasRedoActions();
		}
		if (nonNull(parameter)) {
			extended = parameter.isExtended();
			hasGrid = parameter.showGrid();
			zoomedIn = parameter.isZoomMode();
		}

		undoButton.setEnabled(hasUndo);
		redoButton.setEnabled(hasRedo);
		extendButton.setSelected(extended);
		gridButton.setSelected(hasGrid);
		panButton.setEnabled(zoomedIn);
		zoomOutButton.setEnabled(zoomedIn);
	}

	@Override
	public void setScreensAvailable(boolean screensAvailable) {
		SwingUtils.invoke(() -> displaysButton.setEnabled(screensAvailable));
	}

	@Override
	public void setPresentationViewsVisible(boolean viewsVisible) {
		SwingUtils.invoke(() -> displaysButton.setSelected(viewsVisible));
	}

	@Override
	public void setRecordingState(ExecutableState state) {
		SwingUtils.invoke(() -> {
			boolean started = state == ExecutableState.Started ||
					state == ExecutableState.Suspended;

			startRecordingButton.setState(state);
			stopRecordingButton.setEnabled(started);
		});
	}


	@Override
	public void showRecordNotification(boolean show) {
		SwingUtils.invoke(() -> startRecordingButton.setBlink(show));
	}

	@Override
	public void setOnUndo(Action action) {
		SwingUtils.bindAction(undoButton, action);
	}

	@Override
	public void setOnRedo(Action action) {
		SwingUtils.bindAction(redoButton, action);
	}

	@Override
	public void setOnPreviousSlide(Action action) {
		SwingUtils.bindAction(prevSlideButton, action);
	}

	@Override
	public void setOnNextSlide(Action action) {
		SwingUtils.bindAction(nextSlideButton, action);
	}

	@Override
	public void setOnCustomPaletteColor(ConsumerAction<Color> action) {
		this.paletteColorAction = action;
	}

	@Override
	public void setOnCustomColor(Action action) {
		SwingUtils.bindAction(customColorButton, action);
	}

	@Override
	public void setOnColor1(Action action) {
		SwingUtils.bindAction(colorButton1, action);
	}

	@Override
	public void setOnColor2(Action action) {
		SwingUtils.bindAction(colorButton2, action);
	}

	@Override
	public void setOnColor3(Action action) {
		SwingUtils.bindAction(colorButton3, action);
	}

	@Override
	public void setOnColor4(Action action) {
		SwingUtils.bindAction(colorButton4, action);
	}

	@Override
	public void setOnColor5(Action action) {
		SwingUtils.bindAction(colorButton5, action);
	}

	@Override
	public void setOnColor6(Action action) {
//		SwingUtils.bindAction(colorButton6, action);
	}

	@Override
	public void setOnPenTool(Action action) {
		SwingUtils.bindAction(penButton, action);
		penButton.addChangeListener(e -> {
			if (penButton.isSelected()) {
				setColorButtonsEnabled(true);
			}
		});
	}

	@Override
	public void setOnHighlighterTool(Action action) {
		SwingUtils.bindAction(highlighterButton, action);
		highlighterButton.addChangeListener(e -> {
			if (highlighterButton.isSelected()) {
				setColorButtonsEnabled(true);
			}
		});
	}

	@Override
	public void setOnPointerTool(Action action) {
		SwingUtils.bindAction(pointerButton, action);
		pointerButton.addChangeListener(e -> {
			if (pointerButton.isSelected()) {
				setColorButtonsEnabled(true);
			}
		});
	}

	@Override
	public void setOnTextSelectTool(Action action) {
		SwingUtils.bindAction(textSelectButton, action);
	}

	@Override
	public void setOnLineTool(Action action) {
		SwingUtils.bindAction(lineButton, action);
		lineButton.addChangeListener(e -> {
			if (lineButton.isSelected()) {
				setColorButtonsEnabled(true);
			}
		});
	}

	@Override
	public void setOnArrowTool(Action action) {
		SwingUtils.bindAction(arrowButton, action);
		arrowButton.addChangeListener(e -> {
			if (arrowButton.isSelected()) {
				setColorButtonsEnabled(true);
			}
		});
	}

	@Override
	public void setOnRectangleTool(Action action) {
		SwingUtils.bindAction(rectangleButton, action);
		rectangleButton.addChangeListener(e -> {
			if (rectangleButton.isSelected()) {
				setColorButtonsEnabled(true);
			}
		});
	}

	@Override
	public void setOnEllipseTool(Action action) {
		SwingUtils.bindAction(ellipseButton, action);
		ellipseButton.addChangeListener(e -> {
			if (ellipseButton.isSelected()) {
				setColorButtonsEnabled(true);
			}
		});
	}

	@Override
	public void setOnSelectTool(Action action) {
//		SwingUtils.bindAction(selectButton, action);
	}

	@Override
	public void setOnEraseTool(Action action) {
		SwingUtils.bindAction(eraseButton, action);
		eraseButton.addChangeListener(e -> {
			if (eraseButton.isSelected()) {
				setColorButtonsEnabled(false);
			}
		});
	}

	@Override
	public void setOnTextTool(Action action) {
		SwingUtils.bindAction(textButton, action);
		textButton.addChangeListener(e -> {
			if (textButton.isSelected()) {
				setColorButtonsEnabled(true);
			}
		});
	}

	@Override
	public void setOnTextBoxFont(ConsumerAction<Font> action) {
		this.textBoxFontAction = action;
	}

	@Override
	public void setOnTeXTool(Action action) {
//		SwingUtils.bindAction(texButton, action);
//
//		texButton.addChangeListener(e -> {
//			if (texButton.isSelected()) {
//				setColorButtonsEnabled(true);
//			}
//		});
	}

	@Override
	public void setOnTeXBoxFont(ConsumerAction<TeXFont> action) {
		this.texBoxFontAction = action;
	}

	@Override
	public void setOnClearTool(Action action) {
		SwingUtils.bindAction(clearButton, action);
	}

	@Override
	public void setOnShowGrid(Action action) {
		SwingUtils.bindAction(gridButton, action);
	}

	@Override
	public void setOnExtend(Action action) {
		SwingUtils.bindAction(extendButton, action);
	}

	@Override
	public void setOnWhiteboard(Action action) {
		SwingUtils.bindAction(whiteboardButton, action);
	}

	@Override
	public void setOnEnableDisplays(ConsumerAction<Boolean> action) {
		SwingUtils.bindAction(displaysButton, action);
	}

	@Override
	public void setOnZoomInTool(Action action) {
		SwingUtils.bindAction(zoomInButton, action);
		zoomInButton.addChangeListener(e -> {
			if (zoomInButton.isSelected()) {
				setColorButtonsEnabled(false);
			}
		});
	}

	@Override
	public void setOnZoomOutTool(Action action) {
		SwingUtils.bindAction(zoomOutButton, action);
	}

	@Override
	public void setOnPanTool(Action action) {
		SwingUtils.bindAction(panButton, action);
		panButton.addChangeListener(e -> {
			if (panButton.isSelected()) {
				setColorButtonsEnabled(false);
			}
		});
	}

	@Override
	public void setOnStartRecording(Action action) {
		SwingUtils.bindAction(startRecordingButton, action);
	}

	@Override
	public void setOnStopRecording(Action action) {
		SwingUtils.bindAction(stopRecordingButton, action);
	}

	@Override
	public void selectColorButton(ToolType toolType, PaintSettings settings) {
		SwingUtils.invoke(() -> {
			Enumeration<AbstractButton> colorIter = colorGroup.getElements();
			int index = 0;

			while (colorIter.hasMoreElements()) {
				AbstractButton button = colorIter.nextElement();
				Color color = ColorPalette.getColor(toolType, index++);

				if (isNull(color)) {
					throw new IllegalArgumentException("No color assigned to the color-button");
				}

				// Select button with assigned brush color.
				if (nonNull(settings) && color.equals(settings.getColor())) {
					colorGroup.setSelected(button.getModel(), true);
				}

				setButtonColor(button, ColorConverter.INSTANCE.to(color));
			}
		});
	}

	@Override
	public void selectToolButton(ToolType toolType) {
		SwingUtils.invoke(() -> {
			Enumeration<AbstractButton> toolIter = toolGroup.getElements();

			while (toolIter.hasMoreElements()) {
				AbstractButton button = toolIter.nextElement();
				Object userData = button.getClientProperty("tool");

				if (isNull(userData)) {
					continue;
				}

				// Mapping may contain multiple type entries.
				String[] types = userData.toString().split(",");

				if (types.length == 1) {
					ToolType type = ToolType.valueOf(types[0].trim());

					if (toolType == type) {
						toolGroup.setSelected(button.getModel(), true);
						break;
					}
				}
				else {
					// Handle multiple type entries.
					for (String type : types) {
						ToolType buttonType = ToolType.valueOf(type.trim());

						if (toolType == buttonType) {
							toolGroup.setSelected(button.getModel(), true);

							if (button instanceof ToolGroupButton) {
								ToolGroupButton groupButton = (ToolGroupButton) button;
								groupButton.selectToolType(toolType);
							}
							break;
						}
					}
				}
			}
		});

		customColorButton.getChooser().setToolType(toolType);
	}

	@Override
	public void openCustomizeToolbarDialog() {
		ButtonModel selectedTool = toolGroup.getSelection();
		ButtonModel selectedColor = colorGroup.getSelection();
		toolGroup.clearSelection();
		colorGroup.clearSelection();

		customizedToolbar.displayDialog((int) (getWidth() / 3.5));

		toolGroup.setSelected(selectedTool, true);
		colorGroup.setSelected(selectedColor, true);
	}
	private void setColorButtonsEnabled(boolean enabled) {
		customColorButton.setEnabled(enabled);
		colorButton1.setEnabled(enabled);
		colorButton2.setEnabled(enabled);
		colorButton3.setEnabled(enabled);
		colorButton4.setEnabled(enabled);
		colorButton5.setEnabled(enabled);
	}

	private void setButtonColor(AbstractButton button, Paint paint) {
		int size = undoButton.getIcon().getIconHeight();
		int paintSize = size / 2 + 1;
		int paintOffset = paintSize / 2 - 1;

		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setPaint(paint);
		g2d.fillRect(paintOffset, paintOffset, paintSize, paintSize);
		g2d.setPaint(java.awt.Color.LIGHT_GRAY);
		g2d.drawRect(paintOffset - 2, paintOffset - 2, paintSize + 3, paintSize + 3);
		g2d.dispose();

		button.setIcon(new ImageIcon(image));
	}

	@ViewPostConstruct
	private void initialize() {
		colorGroup = new ButtonGroup();
		toolGroup = new ButtonGroup();

		List<String> defaultToolNames = new ArrayList<>();

		var components = getComponents();
		var jComponents = Arrays.copyOf(components, components.length,
				JComponent[].class);

		for (JComponent component : jComponents) {
			String group = (String) component.getClientProperty("group");
			String id = (String) component.getClientProperty("id");
			String defaultTool = (String) component.getClientProperty("defaultTool");

			if (nonNull(id)) {
				component.setName(id);

				if (nonNull(defaultTool)) {
					defaultToolNames.add(id);
				}
			}

			component.setBackground(null);

			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);

				if (nonNull(group)) {
					if (group.equals("colorGroup")) {
						colorGroup.add((AbstractButton) component);
					}
					else if (group.equals("toolGroup")) {
						toolGroup.add((AbstractButton) component);
					}
				}
			}
			else if (component instanceof JSeparator) {
				String type = (String) component.getClientProperty("type");

				if (nonNull(type) && nonNull(defaultTool)) {
					defaultToolNames.add(type.replace("t", "\t"));
				}
			}
		}

		removeAll();

		jComponents = Arrays.stream(jComponents)
				.filter(Predicate.not(JSeparator.class::isInstance))
				.toArray(JComponent[]::new);

		customizedToolbar = new CustomizedToolbar(jComponents,
				defaultToolNames.toArray(new String[0]), "default",
				resourceBundle, toolController, colorGroup, toolGroup);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = -1;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;

		add(customizedToolbar, c);

		customColorButton.addItemChangeListener(stroke -> {
			Color color = stroke.getColor();

			setButtonColor(customColorButton, ColorConverter.INSTANCE.to(color));
			executeAction(paletteColorAction, color);
		});
		textButton.addItemChangeListener(font -> {
			executeAction(textBoxFontAction, FontConverter.INSTANCE.from(font));
		});
//		texButton.addItemChangeListener(font -> {
//			executeAction(texBoxFontAction, font);
//		});
	}

}
