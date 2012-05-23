package de.ahjava.t5tree.components.internal;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.runtime.RenderCommand;
import org.apache.tapestry5.runtime.RenderQueue;

public class Rec {
    @Parameter(required=true, defaultPrefix=BindingConstants.LITERAL)
    private String text;

    @Parameter(value="block:nodestart") private RenderCommand nodestart;
    @Parameter(value="block:nodeend")   private RenderCommand nodeend;
    @Parameter(value="block:leaf")      private RenderCommand leaf;

    private static final RenderCommand RENDER_CLOSE_TAG = new RenderCommand() {
        public void render(MarkupWriter writer, RenderQueue queue) {
            writer.end();
        }
    };

    
    private RenderCommand createRecCommand(final String text) {
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                writer.element("div", "style", "margin:10px;");
                queue.push(RENDER_CLOSE_TAG);
                if (text.length() > 0) {
                    queue.push(nodeend);
                    queue.push(createRecCommand(text.substring(1)));
                    queue.push(new RenderCommand() {
                        @Override
                        public void render(MarkupWriter writer, RenderQueue queue) {
                            writer.write(text);
                        }
                    });
                    queue.push(nodestart);

                }
                else {
                    queue.push(leaf);
                }
            }
        };
    }
    
    public Object getRender() {
        return createRecCommand(text);
    }
    
    public String getText() {
        return text;
    }

    public boolean getHasChildText() {
        return text.length() > 1;
    }
    
    public String getChildText() {
        return text.substring(1);
    }
}
