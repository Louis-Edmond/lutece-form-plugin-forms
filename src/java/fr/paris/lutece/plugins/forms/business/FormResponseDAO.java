/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

package fr.paris.lutece.plugins.forms.business;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form objects
 */
public final class FormResponseDAO implements IFormResponseDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_response, id_form, guid, creation_date, update_date FROM forms_response WHERE id_response = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_response ( id_form, guid, creation_date, update_date ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_response WHERE id = ? ";
    private static final String SQL_QUERY_DELETE_BY_FORM = "DELETE FROM forms_response WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_response SET id_question = ?, iteration_number = ? WHERE id = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id, id_question, iteration_number FROM forms_response";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormResponse formResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin );

        try
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formResponse.getFormId( ) );
            daoUtil.setString( nIndex++, formResponse.getGuid( ) );

            Timestamp timestampCurrentTime = new Timestamp( System.currentTimeMillis( ) );
            daoUtil.setTimestamp( nIndex++, timestampCurrentTime );
            daoUtil.setTimestamp( nIndex++, timestampCurrentTime );

            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                formResponse.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        finally
        {
            daoUtil.close( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FormResponse load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        FormResponse formResponse = null;

        if ( daoUtil.next( ) )
        {
            formResponse = new FormResponse( );
            formResponse.setId( daoUtil.getInt( "id_response" ) );
            formResponse.setFormId( daoUtil.getInt( "id_form" ) );
            formResponse.setGuid( daoUtil.getString( "guid" ) );

            Timestamp timestampCreationDate = daoUtil.getTimestamp( "creation_date" );
            formResponse.setDateCreation( timestampCreationDate );
            try
            {
                formResponse.setUpdate( daoUtil.getTimestamp( "update_date" ) );
            }
            catch( AppException exception )
            {
                AppLogService.error( "The update date of the FormResponse si not valid !" );

                formResponse.setUpdate( timestampCreationDate );
            }
        }

        daoUtil.close( );

        return formResponse;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeUpdate( );
        daoUtil.close( );

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( FormResponse formResponse, Plugin plugin )
    {

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormResponse> selectFormResponseList( Plugin plugin )
    {
        List<FormResponse> formResponseList = new ArrayList<FormResponse>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );

        daoUtil.close( );

        return formResponseList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeUpdate( );
        daoUtil.close( );

    }

}
