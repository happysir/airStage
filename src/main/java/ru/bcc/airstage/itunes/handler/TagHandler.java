package ru.bcc.airstage.itunes.handler;

import ru.bcc.airstage.itunes.parser.Tag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for properties in library.xml
 */
interface TagHandler {

    public void key(@NotNull String propertyName);

    public void value(@NotNull Tag propertyValue);
}
