/*******************************************************************************
 * Copyright (c) 2010 Earth System Grid Federation
 * ALL RIGHTS RESERVED. 
 * U.S. Government sponsorship acknowledged.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the <ORGANIZATION> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package esg.security.common;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generic client class to send a SAML request via SOAP/HTTP, and return a SOAP response from the server.
 * This class uses the Apache {@link HttpClient} to execute the actual HTTP invocation.
 */
public class SOAPServiceClient {
	
	private final HttpClient client = new HttpClient();
	
	protected final static Log LOG = LogFactory.getLog(SOAPServiceClient.class);
	
	public String doSoap(final String endpoint, final String soapRequest) {
		
	    final PostMethod method = new PostMethod(endpoint);

		try {
		    		    
		    // insert SOAP request as HTTP request body
			log(soapRequest);
		    final byte[] bytes = soapRequest.getBytes();		    
		    final ByteArrayRequestEntity requestEntity = new ByteArrayRequestEntity(bytes);
		    method.setRequestEntity(requestEntity);
						
		    // Execute the method.
		    int statusCode = client.executeMethod(method);

		    if (statusCode != HttpStatus.SC_OK) {
		    	System.err.println("Method failed: " + method.getStatusLine());
		    }
		    
		    // read response headers
		    final Header[] headers = method.getResponseHeaders();
		    for (final Header header : headers) {
		    	if (LOG.isDebugEnabled()) LOG.debug("Response header name="+header.getName()+" value="+header.getValue());
		    }

		    // read the response body (may be null)
		    byte[] responseBody = method.getResponseBody();
		    final String soapResponse = new String(responseBody);
		    log(soapResponse);
		    
		    return soapResponse;

	    } catch (HttpException e) {
	    	log(e.getMessage());
	    	throw new RuntimeException(e);
	    	
	    } catch (IOException e) {
	    	log(e.getMessage());
	    	throw new RuntimeException(e);
	    	
	    } finally {
	    	// release the connection.
	    	method.releaseConnection();
	    }
		
	}
	
	private void log(final String message) {
		System.out.println(message);
		if (LOG.isDebugEnabled()) LOG.debug(message);
	}

}