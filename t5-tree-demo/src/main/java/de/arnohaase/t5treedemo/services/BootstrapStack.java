package de.arnohaase.t5treedemo.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.internal.TapestryInternalUtils;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.StylesheetLink;


public class BootstrapStack implements JavaScriptStack {
    private final StylesheetLink[] stylesheets;
    private final Asset[] jsLibraries;

    public BootstrapStack(final AssetSource assetSource) {
        stylesheets = new StylesheetLink[] {
            TapestryInternalUtils.assetToStylesheetLink.map(assetSource.getUnlocalizedAsset("context:css/bootstrap.css")),
            TapestryInternalUtils.assetToStylesheetLink.map(assetSource.getUnlocalizedAsset("context:css/bootstrap-responsive.css")),
        };
        
        jsLibraries = new Asset[] {
            assetSource.getUnlocalizedAsset("context:js/bootstrap-dropdown.js")
        };
    }

    public List<String> getStacks() {
        return Collections.emptyList();
    }

    public List<Asset> getJavaScriptLibraries() {
        return Arrays.asList(jsLibraries);
    }

    public List<StylesheetLink> getStylesheets() {
        return Arrays.asList(stylesheets);
    }

    public String getInitialization() {
        return null;
    }
}
