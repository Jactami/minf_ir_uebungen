/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package x;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class BeanExample {
    @Field public final String body;
    @Field public final String title;
    @Field public final List<String> keyWords;

    public BeanExample(String body, String title, List<String> keyWords) {
        this.body = body;
        this.title = title;
        this.keyWords = keyWords;
    }
}
