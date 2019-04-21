package com.newrelic.com.google.gson.internal;

import com.newrelic.com.google.gson.JsonElement;
import com.newrelic.com.google.gson.JsonIOException;
import com.newrelic.com.google.gson.JsonNull;
import com.newrelic.com.google.gson.JsonParseException;
import com.newrelic.com.google.gson.JsonSyntaxException;
import com.newrelic.com.google.gson.internal.bind.TypeAdapters;
import com.newrelic.com.google.gson.stream.JsonReader;
import com.newrelic.com.google.gson.stream.JsonWriter;
import com.newrelic.com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

public final class Streams {

    private static final class AppendableWriter extends Writer {
        private final Appendable appendable;
        private final CurrentWrite currentWrite;

        static class CurrentWrite implements CharSequence {
            char[] chars;

            CurrentWrite() {
            }

            public int length() {
                return this.chars.length;
            }

            public char charAt(int i) {
                return this.chars[i];
            }

            public CharSequence subSequence(int start, int end) {
                return new String(this.chars, start, end - start);
            }
        }

        private AppendableWriter(Appendable appendable) {
            this.currentWrite = new CurrentWrite();
            this.appendable = appendable;
        }

        public void write(char[] chars, int offset, int length) throws IOException {
            this.currentWrite.chars = chars;
            this.appendable.append(this.currentWrite, offset, offset + length);
        }

        public void write(int i) throws IOException {
            this.appendable.append((char) i);
        }

        public void flush() {
        }

        public void close() {
        }
    }

    public static JsonElement parse(JsonReader reader) throws JsonParseException {
        boolean isEmpty = true;
        try {
            reader.peek();
            isEmpty = false;
            return (JsonElement) TypeAdapters.JSON_ELEMENT.read(reader);
        } catch (EOFException e) {
            if (isEmpty) {
                return JsonNull.INSTANCE;
            }
            throw new JsonSyntaxException(e);
        } catch (MalformedJsonException e2) {
            throw new JsonSyntaxException(e2);
        } catch (IOException e22) {
            throw new JsonIOException(e22);
        } catch (NumberFormatException e222) {
            throw new JsonSyntaxException(e222);
        }
    }

    public static void write(JsonElement element, JsonWriter writer) throws IOException {
        TypeAdapters.JSON_ELEMENT.write(writer, element);
    }

    public static Writer writerForAppendable(Appendable appendable) {
        return appendable instanceof Writer ? (Writer) appendable : new AppendableWriter(appendable);
    }
}