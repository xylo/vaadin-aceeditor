package org.vaadin.aceeditor;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import de.digitalculture.scalaeval.ScalaScriptEngine;

import javax.script.ScriptEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;


@Theme("reindeer")
@StyleSheet("ace-markers.css")
@SuppressWarnings("serial")
@PreserveOnRefresh
public class AceScalaEditorDemo extends UI {

	//private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	private final ScriptEngine scriptEngine = new ScalaScriptEngine();
	private final AceEditor editor = new AceEditor();

	@Override
	protected void init(final VaadinRequest request) {
		String initialEditorContent = new Scanner(AceScalaEditorDemo.class.getResourceAsStream("initialEditorContent.txt"), "UTF-8").useDelimiter("\\A").next();

		editor.setValue(initialEditorContent);
		editor.setSizeFull();
		editor.setMode(AceMode.scala);
		editor.focus();

		final VerticalSplitPanel split = new VerticalSplitPanel() {{
			setSizeFull();
			setSplitPosition(60f);

			setFirstComponent(editor);
			setSecondComponent(createResultComponent());
		}};

		setContent(split);
	}

	private Component createResultComponent() {
		return new VerticalLayout() {{
			setSizeFull();

			final TextArea ta = new TextArea() {{
				setSizeFull();
				setRows(10);
			}};

			final Button executeButton = new Button("Execute (Control+ENTER)") {{
				setWidth("100%");

				addClickListener(new ClickListener() {
					@Override
					public void buttonClick(final ClickEvent event) {
						handleExecution(ta);
					}
				});

				setClickShortcut(ShortcutAction.KeyCode.ENTER, ShortcutAction.ModifierKey.CTRL);
			}};

			addComponent(executeButton);
			addComponent(ta);
			setExpandRatio(ta, 1);
		}};
	}

	private String stackTraceAsString(final Throwable e) {
		e.printStackTrace();
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(bout));
		try {
			bout.close();
		} catch (final IOException ignored) {
		}

		final String stacktraceAsString = new String(bout.toByteArray());
		return stacktraceAsString;
	}

	private void handleExecution(final TextArea ta) {
		try {
			final Object result = scriptEngine.eval(editor.getValue());
			ta.setValue(result.toString());
		} catch (final Throwable e) {
			ta.setValue(stackTraceAsString(e));
		}
	}

}
