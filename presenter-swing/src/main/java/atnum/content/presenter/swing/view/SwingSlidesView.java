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

import atnum.content.core.app.dictionary.Dictionary;
import atnum.content.core.beans.DoubleProperty;
import atnum.content.core.controller.RenderController;
import atnum.content.core.geometry.Matrix;
import atnum.content.core.input.KeyEvent;
import atnum.content.core.model.Document;
import atnum.content.core.model.DocumentOutlineItem;
import atnum.content.core.model.Page;
import atnum.content.core.view.*;
import atnum.content.core.view.Action;
import atnum.content.presenter.api.config.SlideViewConfiguration;
import atnum.content.presenter.api.stylus.StylusHandler;
import atnum.content.presenter.api.view.SlidesView;
import atnum.content.presenter.swing.input.StylusListener;
import atnum.content.swing.components.*;
import atnum.content.swing.components.SlideView;
import org.lecturestudio.stylus.awt.AwtStylusManager;
import atnum.content.swing.converter.KeyEventConverter;
import atnum.content.swing.converter.MatrixConverter;
import atnum.content.swing.model.AdaptiveTab;
import atnum.content.swing.model.AdaptiveTabType;
import atnum.content.swing.util.AdaptiveTabbedPaneChangeListener;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;
import atnum.content.swing.view.ViewPostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@SwingView(name = "main-slides")
public class SwingSlidesView extends JPanel implements SlidesView {

	private static final String MENU_LABEL_KEY = "menu.contents";

	private final Dictionary dict;

	private ConsumerAction<KeyEvent> keyAction;

	private ConsumerAction<Document> selectDocumentAction;

	private ConsumerAction<DocumentOutlineItem> outlineAction;

	private ConsumerAction<Page> selectPageAction;

	private ConsumerAction<Matrix> viewTransformAction;

	private Action newPageAction;

	private Action deletePageAction;

	private boolean extendedFullscreen;

	private RenderController pageRenderer;

	private JSplitPane tabSplitPane;

	private SlideView slideView;

	private Box rightVbox;

	private AdaptiveTabbedPane rightTabPane;


	private JScrollPane outlinePane;

	private StylusListener stylusListener;


	private double oldTabSplitPaneDividerRatio = 0.9;

	private final boolean currentSpeech = false;


	private final AdaptiveTabbedPane externalSlidePreviewTabPane = new AdaptiveTabbedPane(SwingConstants.RIGHT);


	private String selectedSlideLabelText = "";


	@Inject
	SwingSlidesView(Dictionary dictionary  ) {
		super();
		this.dict = dictionary;

	}

	@Override
	public void setSlideViewConfig(SlideViewConfiguration viewConfig) {
		tabSplitPane.setDividerLocation(viewConfig.getRightSliderPosition());
		oldTabSplitPaneDividerRatio = viewConfig.getRightSliderPosition();
		observeDividerLocation(tabSplitPane, viewConfig.rightSliderPositionProperty());

	}

	@Override
	public void addPageObjectView(PageObjectView<?> objectView) {

		slideView.addPageObjectView(objectView);
	}

	@Override
	public void removePageObjectView(PageObjectView<?> objectView) {
		slideView.removePageObjectView(objectView);
	}

	@Override
	public void removeAllPageObjectViews() {
		slideView.removeAllPageObjectViews();
	}

	@Override
	public List<PageObjectView<?>> getPageObjectViews() {
		return slideView.getPageObjectViews();
	}

	@Override
	public void addDocument(Document doc, PresentationParameterProvider ppProvider) {
		SwingUtils.invoke(() -> {
			final AdaptiveTabbedPane slidesTabPane = getSlidesTabPane();

			// Select document tab.
			int tabCount = slidesTabPane.getPaneTabCount();

			for (int i = 0; i < tabCount; i++) {
				final Component tabComponent = slidesTabPane.getPaneComponentAt(i);
				if (!(tabComponent instanceof ThumbPanel)) {
					continue;
				}

				ThumbPanel thumbnailPanel = (ThumbPanel) tabComponent;

				if (thumbnailPanel.getDocument().getName().equals(doc.getName())) {
					// Reload if document has changed.
					if (!thumbnailPanel.getDocument().equals(doc)) {
						// Prevent tab switching for quiz reloading.
						thumbnailPanel.setDocument(doc, ppProvider);
					}
					return;
				}
			}

			// Create a ThumbnailPanel for each document.
			ThumbnailPanel thumbPanel;

			if (doc.isWhiteboard()) {
				WhiteboardThumbnailPanel wbThumbPanel = new WhiteboardThumbnailPanel(dict);
				wbThumbPanel.setOnAddPage(newPageAction);
				wbThumbPanel.setOnRemovePage(deletePageAction);

				thumbPanel = wbThumbPanel;
			}

			else if (doc.isScreen()) {
				ScreenThumbnailPanel screenThumbPanel = new ScreenThumbnailPanel(dict);
				thumbPanel = screenThumbPanel;
			}
			else {
				thumbPanel = new ThumbnailPanel();
			}

			thumbPanel.setRenderController(pageRenderer);
			thumbPanel.setDocument(doc, ppProvider);
			thumbPanel.addSelectedSlideChangedListener(event -> {
				if (event.getNewValue() instanceof Page) {
					Page page = (Page) event.getNewValue();

					executeAction(selectPageAction, page);
				}
			});

			VerticalTab tab = VerticalTab.fromText(doc.getName(), getSlidesTabPane().getTabPlacement());
			getSlidesTabPane().addTabBefore(new AdaptiveTab(AdaptiveTabType.SLIDE, tab, thumbPanel),
					AdaptiveTabType.MESSAGE);
			getSlidesTabPane().setPaneTabSelected(tab.getText());
			selectedSlideLabelText = tab.getText();
		});
	}

	@Override
	public void removeDocument(Document doc) {
		final AdaptiveTabbedPane slidesTabPane = getSlidesTabPane();

		// Remove document tab.
		for (final AdaptiveTab tab : slidesTabPane.getTabs()) {
			if (!(tab.getComponent() instanceof ThumbPanel)) {
				continue;
			}

			ThumbPanel thumbnailPanel = (ThumbPanel) tab.getComponent();
			if (thumbnailPanel.getDocument().equals(doc)) {
				slidesTabPane.removeTab(tab.getLabelText());
				break;
			}
		}
	}

	private void checkIfThumbSelected() {
		final Component selectedComponent = getSlidesTabPane().getSelectedComponent();

		if (!(selectedComponent instanceof ThumbPanel)) {
			return;
		}

		ThumbPanel thumbPanel = (ThumbPanel) selectedComponent;

		executeAction(selectDocumentAction, thumbPanel.getDocument());
	}

	@Override
	public void selectDocument(Document doc, PresentationParameterProvider ppProvider) {
		SwingUtils.invoke(() -> {
			final AdaptiveTabbedPane slidesTabPane = getSlidesTabPane();

			// Select document tab.
			int tabCount = slidesTabPane.getPaneTabCount();

			for (int i = 0; i < tabCount; i++) {
				final Component tabComponent = slidesTabPane.getPaneComponentAt(i);

				if (!(tabComponent instanceof ThumbPanel)) {
					continue;
				}

				ThumbPanel thumbnailPanel = (ThumbPanel) tabComponent;

				if (thumbnailPanel.getDocument().getName().equals(doc.getName())) {
					// Reload if document has changed.
					if (!thumbnailPanel.getDocument().equals(doc)) {
						// Prevent tab switching for quiz reloading.
						thumbnailPanel.setDocument(doc, ppProvider);
					}
					else {
						slidesTabPane.setPaneTabSelected(doc.getName());
						selectedSlideLabelText = doc.getName();
					}
					break;
				}
			}
		});
	}

	@Override
	public Page getPage() {
		return slideView.getPage();
	}

	@Override
	public void setPage(Page page, PresentationParameter parameter) {
		SwingUtils.invoke(() -> {
			slideView.parameterChanged(page, parameter);
			slideView.setPage(page);

			// Select page on the thumbnail panel.
			final Component selectedComponent = getSlidesTabPane().getSelectedComponent();
			if (selectedComponent instanceof ThumbPanel) {
				ThumbPanel thumbPanel = (ThumbPanel) selectedComponent;
				thumbPanel.selectPage(page);

			}
		});
	}

	@Override
	public void setPageRenderer(RenderController pageRenderer) {
		this.pageRenderer = pageRenderer;
		slideView.setPageRenderer(pageRenderer);
	}

	@Override
	public void setExtendedFullscreen(boolean extended) {
		extendedFullscreen = extended;
		if (isNull(getParent())) {
			return;
		}
		JFrame window = (JFrame) SwingUtilities.getWindowAncestor(this);
	}

	@Override
	public void setStylusHandler(StylusHandler handler){
		stylusListener = new StylusListener(handler, slideView);
		AwtStylusManager manager = AwtStylusManager.getInstance();
		manager.attachStylusListener(slideView, stylusListener);
	}

	@Override
	public void setLaTeXText(String text) {
		//SwingUtils.invoke(() -> latexTextArea.setText(text));
	}
	@Override
	public void setOnKeyEvent(ConsumerAction<KeyEvent> action) {
		keyAction = action;
	}

	@Override
	public void setOnSelectDocument(ConsumerAction<Document> action) {
		selectDocumentAction = action;
	}

	@Override
	public void setOnSelectPage(ConsumerAction<Page> action) {
		selectPageAction = action;
	}

	@Override
	public void setOnViewTransform(ConsumerAction<Matrix> action) {
		viewTransformAction = action;
	}

	@Override
	public void setOnNewPage(Action action) {
		newPageAction = action;
	}

	@Override
	public void setOnDeletePage(Action action) {
		deletePageAction = action;
	}

	@Override
	public void setOnOutlineItem(ConsumerAction<DocumentOutlineItem> action) {
		outlineAction = action;
	}

	private AdaptiveTabbedPane getSlidesTabPane() {
		return rightTabPane;
	}


	private void minimizeRightTabPane() {
		minimizeRightTabPane(false);
	}

	private void minimizeRightTabPane(boolean saveOldRatio) {
		if (currentSpeech  ) {
			return;
		}

		minimizePane(tabSplitPane, isRightTabPaneMinimized(),
				tabSplitPane.getWidth() - tabSplitPane.getDividerSize() - rightTabPane.getPaneMainAxisSize(),
				() -> oldTabSplitPaneDividerRatio = getTabSplitPaneDividerRatio(), saveOldRatio);
	}

	private void maximizeRightTabPane() {
		maximizePane(tabSplitPane, oldTabSplitPaneDividerRatio, tabSplitPane.getWidth());
	}

	private boolean isRightTabPaneMinimized() {
		return rightTabPane.getWidth() <= rightTabPane.getPaneMainAxisSize();
	}

	private void minimizePane(JSplitPane splitPane, boolean isMinimized, int minimizedDividerLocation,
							  Runnable updateSplitPaneRatioFunc, boolean saveOldRatio) {
		if (isMinimized && saveOldRatio) {
			return;
		}

		if (saveOldRatio) {
			updateSplitPaneRatioFunc.run();
		}

		splitPane.setDividerLocation(minimizedDividerLocation);
	}

	private void maximizePane(JSplitPane splitPane, double oldSplitPaneRatio, int splitPaneSize) {
		final int dividerLocation = (int) (oldSplitPaneRatio * splitPaneSize);

		splitPane.setDividerLocation(dividerLocation);
	}

	private double getTabSplitPaneDividerRatio() {
		return tabSplitPane.getDividerLocation() / (double) tabSplitPane.getWidth();
	}

	private void toggleRightTab(boolean sameTab) {
		toggleTab(sameTab, rightTabPane::getWidth, rightTabPane::getPaneMainAxisSize, this::minimizeRightTabPane,
				this::maximizeRightTabPane);
	}

	private void toggleTab(boolean sameTab, IntSupplier tabPaneSizeFunc, IntSupplier tabSizeFunc,
						   Consumer<Boolean> minimizeFunc, Runnable maximizeFunc) {
		final int tabPaneSize = tabPaneSizeFunc.getAsInt();
		final int tabSize = tabSizeFunc.getAsInt();

		if (sameTab) {
			if (tabPaneSize > tabSize) {
				minimizeFunc.accept(true);
			}
			else {
				maximizeFunc.run();
			}
		}
	}

	@ViewPostConstruct
	private void initialize() {
		KeyboardFocusManager keyboardManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		keyboardManager.addKeyEventDispatcher(event -> {
			Component focusOwner = event.getComponent();

			if (nonNull(focusOwner) && focusOwner.isShowing()) {
				if (focusOwner instanceof JTextComponent) {
					return false;
				}
			}
			if (event.getID() == java.awt.event.KeyEvent.KEY_PRESSED) {
				executeAction(keyAction, KeyEventConverter.INSTANCE.from(event));
			}
			return false;
		});

		slideView.addPropertyChangeListener("transform", e -> {
			if (nonNull(viewTransformAction)) {
				executeAction(viewTransformAction, MatrixConverter.INSTANCE.from(slideView.getPageTransform()));
			}
		});


		rightTabPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					checkIfThumbSelected();
				}
			}
		});

		rightTabPane.addChangeListener(new AdaptiveTabbedPaneChangeListener() {
			@Override
			public void onTabAdded(boolean visibleOrEnabled) {
				if (!visibleOrEnabled) {
					rightTabPane.setPaneTabSelected(selectedSlideLabelText);
				}
			}

			@Override
			public void onTabRemoved() {
				rightTabPane.setPaneTabSelected(selectedSlideLabelText);
			}

			@Override
			public void onTabClicked(AdaptiveTab clickedTab, boolean sameTab) {
				if (clickedTab.type == AdaptiveTabType.SLIDE) {
					selectedSlideLabelText = clickedTab.getLabelText();
				}
				toggleRightTab(sameTab);
			}

			@Override
			public void onVisibilityChanged(boolean visible) {
				if (visible) {
					maximizeRightTabPane();
				}
				else {
					minimizeRightTabPane();
				}
			}

			@Override
			public void onNoTabsEnabled() {
				minimizeRightTabPane();
			}
		});

		externalSlidePreviewTabPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					checkIfThumbSelected();
				}
			}
		});
		externalSlidePreviewTabPane.addChangeListener(new AdaptiveTabbedPaneChangeListener() {
			@Override
			public void onTabClicked(AdaptiveTab clickedTab, boolean sameTab) {
				selectedSlideLabelText = clickedTab.getLabelText();
			}
		});

		addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorAdded(AncestorEvent event) {
				JFrame window = (JFrame) SwingUtilities.getWindowAncestor(SwingSlidesView.this);
				window.addComponentListener(new ComponentAdapter() {

					@Override
					public void componentShown(ComponentEvent e) {
						AwtStylusManager manager = AwtStylusManager.getInstance();
						manager.attachStylusListener(slideView, stylusListener);
					}
				});
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
				removeAncestorListener(this);
			}
		});
	}

	private void observeDividerLocation(final JSplitPane pane,
			final DoubleProperty property) {
		BasicSplitPaneUI ui = (BasicSplitPaneUI) pane.getUI();
		BasicSplitPaneDivider divider = ui.getDivider();

		divider.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (pane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
					property.set(pane.getDividerLocation() / (double) pane.getWidth());
				}
				else {
					property.set(pane.getDividerLocation() / (double) pane.getHeight());
				}
			}
		});
	}
}
