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

package fr.prima.omiscid.control.interf;


public final class GlobalConstants {

    public static final String controlAnswerXMLTag = "controlAnswer";

    public static final String dnssdDefaultWorkingDomain = "_bip._tcp";
    public static final String dnssdWorkingDomainEnvironmentVariableName = "OMISCID_WORKING_DOMAIN";

    public static final String constantNameForPeerId = "peerId";
    public static final String constantTypeForPeerId = "hexadecimal";
    public static final String constantDescriptionForPeerId = "PeerId of this service";
    
    public static final String constantNameForName = "name";
    public static final String constantTypeForName = "string";
    public static final String constantDescriptionForName = "Registered name of this service";
        
    public static final String constantNameForOwner = "owner";
    public static final String constantTypeForOwner = "string";
    public static final String constantDescriptionForOwner = "Login which launches this service";
    
    public static final String constantNameForClass = "class";
    public static final String constantTypeForClass = "class";
    public static final String constantDescriptionForClass = "Class of this service";
    public static final String defaultServiceClassValue = "Service";
    
    public static final String variableNameForLock = "lock";
    public static final String variableTypeForLock = "integer";
    public static final String variableDescriptionForLock = "Use for locking access";

    public static final String keyForFullTextRecord = "desc";
    public static final String keyForFullTextRecordFull = "full";
    public static final String keyForFullTextRecordNonFull = "part";
    
    public static final String[] specialVariablesNames = new String[] {
        constantNameForOwner,
        constantNameForClass,
        variableNameForLock,
        constantNameForName,
    };

}
