package com.kamomileware.define.model.term;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pepe on 10/07/14.
 */
public enum TermCategory {
    NP("Nombre propio", Formatters.normalFormatter),

    CH("Cheli", Formatters.normalFormatter),

    SG("S.I.G.L.A", Formatters.siglasFormatter),

    AB("Abstracto", Formatters.normalFormatter);

    private String label;
    private TermDefinitionFormatter formatter;

    TermCategory(String label, TermDefinitionFormatter formatter) {
        this.label = label;
        this.formatter = formatter;
    }

    public String format(String text) {
        return formatter.format(text);
    }

    public String getName(){
        return this.name();
    }

    public String getLabel(){
        return this.label;
    }
}

interface TermDefinitionFormatter {
    String format(String text);
}

class Formatters {
    public static TermDefinitionFormatter normalFormatter = new TermDefinitionFormatter() {
        @Override
        public String format(String text) {
            return Joiner.on(". ").join(getFirstUpperList(Splitter.on(".").trimResults().omitEmptyStrings().splitToList(text)));
        }

        public List<String> getFirstUpperList(List<String> list) {
            return list.stream().map(p -> getFirstUpper(p)).collect(Collectors.toList());
        }

        private String getFirstUpper(String text) {
            return text.length()>1? text.substring(0, 1).toUpperCase().concat(text.substring(1)) : text;
        }
    };

    public static TermDefinitionFormatter siglasFormatter = new TermDefinitionFormatter() {
        @Override
        public String format(String text) {
            return WordUtils.capitalize(text);
        }
    };
}
