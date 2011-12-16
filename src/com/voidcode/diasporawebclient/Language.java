package com.voidcode.diasporawebclient;

/**
 * Language.java
 *
 * Copyright (C) 2007,  Richard Midwinter
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 * Defines language information for the Google Translate API.
 *
 * @author Richard Midwinter
 * @author alosii
 * @author bjkuczynski
 * @author Voidcode
 * 
 */
public enum Language {
		YourLanguage("none"),
        ALBANIAN("sq"),
        ARABIC("ar"),
        BELARUSIAN("be"),
        BULGARIAN("bg"),
        CATALAN("ca"),
        CHINESE("zh"),
        CHINESE_SIMPLIFIED("zh-CN"),
        CHINESE_TRADITIONAL("zh-TW"),
        CROATIAN("hr"),
        CZECH("cs"),
        DANISH("da"),
        DUTCH("nl"),
        ENGLISH("en"),
        ESTONIAN("et"),
        FILIPINO("tl"),
        FINNISH("fi"),
        FRENCH("fr"),
        GALICIAN("gl"),
        GERMAN("de"),
        GREEK("el"),
        HEBREW("iw"),
        HINDI("hi"), //maby some errors
        HUNGARIAN("hu"),
        ICELANDIC("is"),
        INDONESIAN("id"),
        IRISH("ga"),
        ITALIAN("it"),
        JAPANESE("ja"),
        KOREAN("ko"),
        LATVIAN("lv"),
        LITHUANIAN("lt"),
        MACEDONIAN("mk"),
        MALAY("ms"),
        MALTESE("mt"),
        NORWEGIAN("no"),
        PERSIAN("fa"),
        POLISH("pl"),
        PORTUGUESE("pt"),
        ROMANIAN("ro"),
        RUSSIAN("ru"),
        SERBIAN("sr"),
        SLOVAK("sk"),
        SLOVENIAN("sl"),
        SPANISH("es"),
        SWAHILI("sw"),
        SWEDISH("sv"),
        TAGALOG("tl"),
        THAI("th"),
        TURKISH("tr"),
        UKRANIAN("uk"),
        VIETNAMESE("vi"),
        WELSH("cy"),
        YIDDISH("yi");
        
        /**
         * Google's String representation of this language.
         */
        private final String language;
        /**
         * Enum constructor.
         * @param pLanguage The language identifier.
         */
        private Language(final String pLanguage) {
                this.language = pLanguage;
        }
        
        public static Language fromString(final String pLanguage) {
                for (Language l : values()) {
                        if (pLanguage.equals(l.toString())) {
                                return l;
                        }
                }
                return null;
        }
        //http://stackoverflow.com/questions/8516603/android-how-to-add-enum-labels-to-a-spinner-not-the-enum-values
        public String shortCode() {
        	  return language;
        }
}