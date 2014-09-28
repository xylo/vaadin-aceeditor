package org.vaadin.aceeditor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.script.ScriptEngine;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

import de.digitalculture.scalaeval.ScalaScriptEngine;


@Theme("reindeer")
@StyleSheet("ace-markers.css")
@SuppressWarnings("serial")
@PreserveOnRefresh
public class AceScalaEditorDemo extends UI {

  //private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
  private final ScriptEngine scriptEngine = new ScalaScriptEngine();
  private final AceEditor editor = new AceEditor();

	public AceScalaEditorDemo() {
		final String s = "val l = List(1,2,3,4,5)\n"
				+ "l.filter(_ % 2 == 0)\n";

		editor.setValue(s);

		editor.focus();

	}

	@Override
	protected void init(final VaadinRequest request) {

		final VerticalSplitPanel split = new VerticalSplitPanel();
		split.setSizeFull();
		setContent(split);
		split.setSplitPosition(70f);

		split.setFirstComponent(editor);

		editor.setSizeFull();
		editor.setMode(AceMode.scala);

		final VerticalLayout valueLayout = new VerticalLayout();
		valueLayout.setMargin(true);
		valueLayout.addComponent(createValueTextArea());

		split.setSecondComponent(valueLayout);


	}

	private Component createValueTextArea() {

		final Button executeButtons = new Button("Execute");

		final VerticalLayout layout = new VerticalLayout();

		final TextArea ta = new TextArea();
		executeButtons.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				handleExecution(ta);
			}
		});
		executeButtons.setWidth("100%");
		layout.addComponent(executeButtons);

		ta.setWidth("100%");
		ta.setHeight("100%");

		layout.addComponent(ta);

		return new Panel("Value", layout);
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
