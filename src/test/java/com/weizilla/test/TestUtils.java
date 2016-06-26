package com.weizilla.test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils
{
    public static String readFile(String filename) throws Exception
    {
        URL url = Resources.getResource(filename);
        String contents = Resources.toString(url, Charsets.UTF_8);
        assertThat(contents).isNotEmpty();
        return contents;
    }

    public static void assertPrivateConstructor(Class<?> clazz) throws Exception
    {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        assertThat(constructor.getModifiers()).isEqualTo(Modifier.PRIVATE);
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
