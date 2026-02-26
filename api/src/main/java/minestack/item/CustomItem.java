package minestack.item;

import org.intellij.lang.annotations.Pattern;

public @interface CustomItem {

    @Pattern("^.*\\.png$")
    String texture();


}
