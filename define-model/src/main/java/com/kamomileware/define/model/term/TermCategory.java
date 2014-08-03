package com.kamomileware.define.model.term;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pepe on 10/07/14.
 */
@Document
public class TermCategory {

    @Id
    private final String name;

    @NotBlank
    private final String label;

    @NotNull
    private TermDefinitionFormatter formatter;

    TermCategory(String name, String label){
        this(name, label, Formatters.normalFormatter);
    }

    TermCategory(String name, String label, TermDefinitionFormatter formatter) {
        this.name = name;
        this.label = label;
        this.formatter = formatter;
    }

    public String format(String text) {
        return formatter.format(text);
    }

    public String getName() {
        return name;
    }

    public String getLabel(){
        return this.label;
    }

    public static final TermCategory NP = new TermCategory("NP", "Nombre propio");

    public static final TermCategory CH = new TermCategory("CH", "Cheli");

    public static final TermCategory SG = new TermCategory("SG", "S.I.G.L.A", Formatters.siglasFormatter);

    public static final TermCategory AB = new TermCategory("AB", "Abstracto");

    public static final TermCategory[] categories = new TermCategory[]{NP,CH,SG,AB};
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

    public static TermDefinitionFormatter siglasFormatter = text -> WordUtils.capitalize(text);
}
