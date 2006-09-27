/**
 * Copyright© 2005-2006 INRIA/Université Pierre Mendès-France/Université Joseph Fourier.
 *
 * O3MiSCID (aka OMiSCID) Software written by Sebastien Pesnel, Dominique
 * Vaufreydaz, Patrick Reignier, Remi Emonet and Julien Letessier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fr.prima.omiscid.user.variable;

import fr.prima.omiscid.user.util.Constants;

public enum VariableAccessType {
    CONSTANT(
            Constants.variableAccessTypeConstant,
            Constants.prefixForConstantInDnssd
            ),
    READ(
            Constants.variableAccessTypeRead,
            Constants.prefixForReadOnlyVariableInDnssd
            ),
    READ_WRITE(
            Constants.variableAccessTypeReadWrite,
            Constants.prefixForReadWriteVariableInDnssd);

    private String stringDescription;
    private String prefixInDnssd;

    private VariableAccessType(String stringDescription, String prefixInDnssd) {
        this.stringDescription = stringDescription;
        this.prefixInDnssd = prefixInDnssd;
    }

    public String getStringDescription() {
        return stringDescription;
    }

    public String getPrefixInDnssd() {
        return prefixInDnssd;
    }

    public static VariableAccessType fromDnssdValue(String propertyValue) {
        if (propertyValue.startsWith(CONSTANT.prefixInDnssd)) {
            return CONSTANT;
        } else if (propertyValue.startsWith(READ.prefixInDnssd)) {
            return READ;
        } else if (propertyValue.startsWith(READ_WRITE.prefixInDnssd)) {
            return READ_WRITE;
        }
        return null;
    }

    public static String realValueFromDnssdValue(String propertyValue) {
        VariableAccessType variableAccessType = fromDnssdValue(propertyValue);
        return variableAccessType == null ?
                null : propertyValue.replaceFirst(variableAccessType.getPrefixInDnssd(), "");
    }

//    public static VariableAccessType fromControlString(String accessTypeName) {
//        if (CONSTANT.stringDescription.equals(accessTypeName)) {
//            return CONSTANT;
//        } else if (READ.stringDescription.equals(accessTypeName)) {
//            return READ;
//        } else if (READ_WRITE.stringDescription.equals(accessTypeName)) {
//            return READ_WRITE;
//        }
//        return null;
//    }
}
