package com.kamomileware.define.model.term;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pepe on 10/07/14.
 */
@Document(collection = "cats") @TypeAlias("cat")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name", resolver = TermCategoryResolver.class)
@JsonTypeName("cat")
public class TermCategory {

    @Id
    private String name;

    @NotBlank
    private String label;

    @NotNull
    private TermDefinitionFormatter formatter;

    public TermCategory(String name, String label){
        this(name, label, Formatters.normalFormatter);
    }

    public TermCategory() {
        this(null, null, Formatters.normalFormatter);
    }

    public TermCategory(String name, String label, TermDefinitionFormatter formatter) {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static final TermCategory NP = new TermCategory("NP", "Nombre propio");

    public static final TermCategory CH = new TermCategory("CH", "Cheli");

    public static final TermCategory SG = new TermCategory("SG", "S.I.G.L.A", Formatters.siglasFormatter);

    public static final TermCategory AB = new TermCategory("AB", "Abstracto");

    public static final TermCategory[] categories = new TermCategory[]{NP,CH,SG,AB};

    public static TermCategory forName(String cat) {
        TermCategory result;
        switch(cat){
            case "NP": result = NP; break;
            case "CH": result = CH; break;
            case "SG": result = SG; break;
            case "AB": result = AB; break;
            default:
                result = new TermCategory(cat, cat);
        };
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TermCategory that = (TermCategory) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
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

    public static TermDefinitionFormatter siglasFormatter = text -> WordUtils.capitalize(text);
}

class TermCategoryResolver implements ObjectIdResolver {

    @Override
    public void bindItem(ObjectIdGenerator.IdKey id, Object pojo) { }

    @Override
    public Object resolveId(ObjectIdGenerator.IdKey id) {
        return TermCategory.forName(id.key.toString());
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return this;
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType.getClass() == getClass();
    }
}