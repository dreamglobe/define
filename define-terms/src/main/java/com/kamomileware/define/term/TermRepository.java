package com.kamomileware.define.actor;

import com.kamomileware.define.model.term.Term;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.kamomileware.define.model.term.TermCategory.*;
import static com.kamomileware.define.model.term.TermCategory.AB;

/**
 * Created by pepe on 17/07/14.
 */
public class TermRepository {

    private static List<Term> terms = Arrays.asList(new Term[]{
            new Term("Ambrosio", "El inmortal", NP),
            new Term("Templo", "Bar de moda", CH),
            new Term("O.M.N.I", "Objeto Marino No Identificado", SG),
            new Term("Ratería", "Bajeza en el trato", AB),
            new Term("Flavio", "Amarillo, dorado, rubio", NP),
            new Term("Entoligar", "Golpear", CH),
            new Term("F.U.E.", "Federacion Universitaria Escolar", SG),
            new Term("Humildad", "Sentimiento de propia inferioridad", AB),
            new Term("Eduvigis", "Lucha, pelea", NP),
            new Term("Chingón", "Colérico, propenso a enfadarse", CH),
            new Term("F.E.T.E.", "Federación Española de Trabajadores de la Enseñanza", SG),
            new Term("Exultar", "Regocijarse, dar saltos de alegría", AB),
            new Term("Job", "Afligido", NP),
            new Term("Gambear", "Pasear", CH),
            new Term("I.R.Y.D.A.", "Instituto Nacional de Reforma y Desarrollo Agrario", SG),
            new Term("Mansedumbre", "Apacibilidad", AB),
            new Term("Fidel", "Tener confianza", NP),
            new Term("Espeta", "Policía del Cuerpo Superior de Policía", CH),
            new Term("F.T.R.E", "Federación de Trabajadores de la Región Española", SG),
            new Term("Humanidad", "Compasión", AB),
            new Term("Reveriano", "Temer a alguien", NP),
            new Term("Macanear", "Hacer o decir tonterías", CH),
            new Term("O.U.A", "Organización de la Unión Africana", SG),
            new Term("Ricamente", "Con opulencia", AB),
            new Term("Jasón", "El que sanará", NP),
            new Term("Flauta", "Jeringuilla", CH),
            new Term("I.N.T.A", "Instituto Nacional de Técnicas Aeroespaciales", SG),
            new Term("Malograr", "Frustrarse", AB),
            new Term("Salomé", "Completo, perfecto", NP),
            new Term("Níquel", "De níquel: muy bien", CH),
            new Term("A.U.", "Alianza Universitaria", SG),
            new Term("Soltura", "Agilidad, desenvoltura", AB),
            new Term("Pamela", "Todo dulzura", NP),
            new Term("Liche", "Calle", CH),
            new Term("A.I.P.", "Acuerdo Intereuropeo de Pagos", SG),
            new Term("Rapiña", "Avidez", AB),
            new Term("Hiram", "El hermano es excelso", NP),
            new Term("Frusa", "Miedo", CH),
            new Term("I.E.F.", "Instituto de Estudios Fiscales", SG),
            new Term("Justipreciar", "Apreciar una cosa", AB),
            new Term("Rut", "Belleza", NP),
            new Term("Nave", "Coche, automóvil", CH),
            new Term("C.I.O.S.L.", "Confederación Internacional de Organizaciones Sindicales Libres", SG),
            new Term("Sobrecoger", "Asustar, intimidar", AB),
            new Term("Muriel", "Resplandeciente", NP),
            new Term("In", "Ser in: estar de moda", CH),
            new Term("R.E.N.F.E.", "Red Nacional de los Ferrocarriles Españoles", SG),
            new Term("Perplejidad", "Estado mental de confusión", AB),
            new Term("Ilidio", "Tropa", NP),
            new Term("Fa", "Dar la fa: avisar de un peligro a quienes cometen un delito", CH),
            new Term("I.E.P.", "Instituto de Estudios Politicos", SG),
            new Term("Lapsus", "Olvido", AB),
            new Term("Tamara", "Palmera de dátiles", NP),
            new Term("Pulguero", "Cama", CH),
            new Term("B.I.R.D", "Banco Internacional para la Reconstrucción y el Desarrollo", SG),
            new Term("Veleidad", "Deseo caprichoso", AB),
            new Term("Palmira", "Rama de la palmera", NP),
            new Term("Julepe", "Juego de cartas", CH),
            new Term("A.I.A.P", "Asociación Internacional de Artes Plásticas", SG),
            new Term("Radiestesia", "Sensibilidad para captar ciertas radiaciones", AB),
            new Term("Sebastián", "Digno de respeto", NP),
            new Term("Orinal", "Casco", CH),
            new Term("B.O.E.", "Boletín Oficial del Estado", SG),
            new Term("Sutileza", "Calidad de sutil", AB),
            new Term("Edgar", "Propiedad, riqueza", NP),
            new Term("Chasis", "El esqueleto", CH),
            new Term("F.E.D.A.S.", "Federación Española de Actividades Subacuáticas", SG),
            new Term("Espeluznar", "Aterrorizar", AB),
            new Term("Diana", "Claridad, cielo", NP),
            new Term("Chupano", "Celda de castigo", CH),
            new Term("E.S.T.E.", "Escuela Superior de Técnica Empresarial", SG),
            new Term("Encarar", "Enfrentarse a alguien o algo ", AB),
            new Term("Nadia", "Esperanza", NP),
            new Term("Iguales", "Pareja de la guardia civil", CH),
            new Term("R.N.E", "Radio Nacional Española", SG),
            new Term("Persistir", "Perseverar", AB),
            new Term("Alfonso", "Listo, preparado", NP),
            new Term("Arajai", "Sacerdote", CH),
            new Term("A.E.P.D", "Asociación Española de Periodistas Deportivos", SG),
            new Term("Angustia", "Malestar psíquico que hace sentir un temblor difuso", AB),
            new Term("Adolfo", "Guerrero de noble estirpe", NP),
            new Term("Agnusdei", "Estar echo un agnusdei: estar muy magullado o herido", CH),
            new Term("A.D.L.A.N.", "Amigos del Arte Nuevo", SG),
            new Term("Acholar", "Avergonzar", AB),
            new Term("Abigail", "Alegría del padre", NP),
            new Term("Abuchear", "Dejar, abandonar, tirar", CH),
            new Term("A.C.T.R.", "Antiguos Combatientes de Tercios de Requetés", SG),
            new Term("Aberración", "Apartamiento de lo que es normal, justo, correcto, lógico", AB),
            new Term("Wally", "Extranjero", NP),
            new Term("Surai", "Ojo", CH),
            new Term("F.I.A.", "Federación Internacional de Astronáutica", SG),
            new Term("Estoicismo", "Resistencia a las adversidades", AB),
//            new Term("", "", NP),
//            new Term("", "", CH),
//            new Term("", "", SG),
//            new Term("", "", AB),
//            new Term("", "", NP),
//            new Term("", "", CH),
//            new Term("", "", SG),
//            new Term("", "", AB),
//            new Term("", "", NP),
//            new Term("", "", CH),
//            new Term("", "", SG),
//            new Term("", "", AB),
//            new Term("", "", NP),
//            new Term("", "", CH),
//            new Term("", "", SG),
//            new Term("", "", AB),
//            new Term("", "", NP),
//            new Term("", "", CH),
//            new Term("", "", SG),
//            new Term("", "", AB),
//            new Term("", "", NP),
//            new Term("", "", CH),
//            new Term("", "", SG),
//            new Term("", "", AB),
    });

    public static Term shuffleAndGet(){
        Collections.shuffle(terms);
        return terms.get(0);
    }

    public static Term get(int i){
        return terms.get(i);
    }
}
