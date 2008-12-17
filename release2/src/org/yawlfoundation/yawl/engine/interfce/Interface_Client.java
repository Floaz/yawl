/*
 * This file is made available under the terms of the LGPL licence.
 * This licence can be retrieved from http://www.gnu.org/copyleft/lesser.html.
 * The source remains the property of the YAWL Foundation.  The YAWL Foundation is a collaboration of
 * individuals and organisations who are committed to improving workflow technology.
 *
 */

package org.yawlfoundation.yawl.engine.interfce;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used by clients and servers to execute GET and POST requests
 * across the YAWL interfaces. Note that since v2.0 (12/08) all requests are sent as
 * POSTS - increases efficiency, security and allows 'extended' chars to be included.
 *
 * @author Lachlan Aldred
 * Date: 22/03/2004
 * Time: 17:49:42
 *
 * @author Michael Adams (refactored for v2.0, 06/2008; and again 12/2008)
 */

public class Interface_Client {

    /**
     * Executes a HTTP POST request on the url specified.
     *
     * @param urlStr the URL to send the POST to
     * @param paramsMap a set of attribute-value pairs that make up the posted data
     * @return the result of the POST request
     * @throws IOException when there's some kind of communication problem
     */
    protected String executePost(String urlStr, Map<String, String> paramsMap)
            throws IOException {

        return send(urlStr, paramsMap, true);
    }


    /**
     * Executes a rerouted HTTP GET request as a POST on the specified URL
     *
     * @param urlStr the URL to send the GET to
     * @param paramsMap a set of attribute-value pairs that make up the posted data
     * @return the result of the request
     * @throws IOException when there's some kind of communication problem
     */
    protected String executeGet(String urlStr, Map<String, String> paramsMap)
            throws IOException {

        return send(urlStr, paramsMap, false);
    }


    /**
     * Initialises a map for transporting parameters - used by extending classes
     * @param action the name of the action to take
     * @param handle the current engine session handle
     * @return the initialised Map
     */
    protected Map<String, String> prepareParamMap(String action, String handle) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("action", action) ;
        if (handle != null) result.put("sessionHandle", handle) ;
        return result;
    }


    /**
     * Removes an outer set of xml tags from an xml string, if possible
     * @param inputXML the xml string to strip
     * @return the stripped xml string
     */
    protected String stripOuterElement(String inputXML) {
        if (inputXML != null) {
            int beginClipping = inputXML.indexOf(">") + 1;
            int endClipping = inputXML.lastIndexOf("<");
            if (beginClipping >= 0 && endClipping >= 0 && endClipping > beginClipping) {
                inputXML = inputXML.substring(beginClipping, endClipping);
            }
        }
        return inputXML;
    }


    /**
     * Tests a response message for success or failure
     * @param message the response message to test
     * @return true if the response represents success
     */
    public boolean successful(String message) {
        return (message != null)  &&
               (message.length() > 0) &&
               (message.indexOf("<failure>") == -1) ;
    }


    /*******************************************************************************/

    // PRIVATE METHODS //

    /**
     * Sends data to the specified url via a HTTP POST, and returns the reply
     * @param urlStr the url to connect to
     * @param paramsMap a map of atttribute=value pairs representing the data to send
     * @param post true if this was originally a POST request, false if a GET request
     * @return the response from the url
     * @throws IOException
     */
    private String send(String urlStr, Map<String, String> paramsMap, boolean post)
            throws IOException {

        // create and setup connection
        HttpURLConnection connection = initPostConnection(urlStr);

        // encode data and send query
        sendData(connection, encodeData(paramsMap)) ;

        //retrieve reply
        String result = getReply(connection.getInputStream());
        connection.disconnect();

        if (post) result = stripOuterElement(result);
        return result;
    }


    /**
     * Initialises a HTTP POST connection
     * @param urlStr the url to connect to
     * @return an initialised POST connection
     * @throws IOException when there's some kind of communication problem
     */
    private HttpURLConnection initPostConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        return connection ;
    }


     /**
     * Encodes parameter values for HTTP transport
     * @param params a map of the data parameter values, of the form
     *        [param1=value1],[param2=value2]...
     * @return a formatted http data string with the data values encoded
     */
    private String encodeData(Map<String, String> params) {
        StringBuilder result = new StringBuilder("");
        for (String param : params.keySet()) {
            if (result.length() > 0) result.append("&");
            result.append(param)
                  .append("=")
                  .append(ServletUtils.urlEncode(params.get(param)));
        }
        return result.toString();
    }


    /**
     * Submits data on a HTTP connection
     * @param connection a valid, open HTTP connection
     * @param data the data to submit
     * @throws IOException when there's some kind of communication problem
     */
    private void sendData(HttpURLConnection connection, String data)
            throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        out.write(data);
        out.flush();
        out.close();
    }


    /**
     * Receives a reply from a HTTP submission
     * @param is the InputStream or a URL or Connection object
     * @return the stream's contents (ie. the HTTP reply)
     * @throws IOException when there's some kind of communication problem
     */
    private String getReply(InputStream is) throws IOException {
        DataInputStream din = new DataInputStream(is);

        // read spec into a byte array wrapped in a ByteBuffer - can't do it in
        // one read because of a buffer size limit in the InputStream
        byte[] specContents = new byte[din.available()];
        ByteBuffer bytes = ByteBuffer.wrap(specContents);
        byte[] buffer = new byte[8192];

        // read chunks from the stream and append them to the ByteBuffer
        int bytesRead;
        while ((bytesRead = din.read(buffer, 0, buffer.length)) > 0) {
            bytes.put(buffer, 0, bytesRead);
        }

        // convert the bytes to a string with the right charset
        return new String(specContents, "UTF-8");
    }
}
