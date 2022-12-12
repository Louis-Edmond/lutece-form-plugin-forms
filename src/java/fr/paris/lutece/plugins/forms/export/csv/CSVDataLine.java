/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.forms.export.csv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class represents a CSV line
 *
 */
public class CSVDataLine
{
    private static final String RESPONSE_SEPARATOR = " ";

    private final Map<Integer, Map<Integer, String>> _mapDataToExport;
    private final String _commonDataToExport;

    /**
     * Constructor
     * 
     * @param formResponse
     *            the form response associated to this instance
     */
    public CSVDataLine( FormResponse formResponse, String state, String csvSeparator )
    {
        _mapDataToExport = new HashMap<>( );

        Locale locale = I18nService.getDefaultLocale( );
        DateFormat dateFormat = new SimpleDateFormat( AppPropertiesService.getProperty( FormsConstants.PROPERTY_EXPORT_FORM_DATE_CREATION_FORMAT ), locale );
        Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
        StringBuilder commonData = new StringBuilder( );
        commonData.append( CSVUtil.safeString( form.getTitle( ) ) ).append( csvSeparator );
        commonData.append( CSVUtil.safeString( dateFormat.format( formResponse.getCreation( ) ) ) ).append( csvSeparator );
        commonData.append( CSVUtil.safeString( dateFormat.format( formResponse.getUpdate( ) ) ) ).append( csvSeparator );
        commonData.append( state ).append( csvSeparator );
        _commonDataToExport = commonData.toString( );
    }

    /**
     * 
     * @param formQuestionResponse
     *            The data to add
     */
    public void addData( FormQuestionResponse formQuestionResponse )
    {
        Question question = formQuestionResponse.getQuestion( );
        IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );

        Map<Integer, List<String>> mapIterationsResponseValue = entryDataService.responseToIterationsStrings( formQuestionResponse );
        StringBuilder sbReponseValues = new StringBuilder( );

        for (Entry<Integer, List<String>> entry : mapIterationsResponseValue.entrySet())
        {
        	Integer iteration = entry.getKey();
        	if (!_mapDataToExport.containsKey(iteration)) {
        		_mapDataToExport.put(iteration, new HashMap<>());
        	}
        	Map<Integer, String> mapQuestionsData = _mapDataToExport.get(iteration);
        	for (String strResponseValue : entry.getValue()) {
    			sbReponseValues.append( strResponseValue ).append( RESPONSE_SEPARATOR );
        	}
        	mapQuestionsData.put(question.getId( ),  CSVUtil.safeString( sbReponseValues.toString( ) ));
        }
    }

    /**
     * @param strColumnName
     *            The column name
     * @return the _mapDataToExport
     */
    public String getDataToExport( Integer iteration, Question question )
    {
        return _mapDataToExport.get(iteration) != null ? _mapDataToExport.get(iteration).get(question.getId()) : null;
    }
    
    public Map<Integer, Map<Integer, String>> getMapDataToExport()
    {
    	return _mapDataToExport;
    }
    
    /*public Map<Integer, String> getMapDataByIteration(Integer iteration)
    {
    	return _mapDataToExport.get(iteration);
    }*/

    public String getCommonDataToExport( )
    {
        return _commonDataToExport;
    }

}
