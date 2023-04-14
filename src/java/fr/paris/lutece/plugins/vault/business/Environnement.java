/*
 * Copyright (c) 2002-2023, City of Paris
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
package fr.paris.lutece.plugins.vault.business;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * The type Environnement.
 */
public class Environnement implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    @Size( max = 50, message = "#i18n{vault.validation.environnement.Code.size}" )
    private String _strCode;

    @NotEmpty( message = "#i18n{vault.validation.environnement.Token.notEmpty}" )
    @Size( max = 50, message = "#i18n{vault.validation.environnement.Token.size}" )
    private String _strToken;

    private String _strPath;

    private int _nIdapplication;

    private String _strType;

    private List<Properties> _listProperties;

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets id.
     *
     * @param nId
     *            the n id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode( )
    {
        return _strCode;
    }

    /**
     * Sets code.
     *
     * @param strCode
     *            the str code
     */
    public void setCode( String strCode )
    {
        _strCode = strCode;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken( )
    {
        return _strToken;
    }

    /**
     * Sets token.
     *
     * @param strToken
     *            the str token
     */
    public void setToken( String strToken )
    {
        _strToken = strToken;
    }

    /**
     * Gets idapplication.
     *
     * @return the idapplication
     */
    public int getIdapplication( )
    {
        return _nIdapplication;
    }

    /**
     * Sets idapplication.
     *
     * @param nIdapplication
     *            the n idapplication
     */
    public void setIdapplication( int nIdapplication )
    {
        _nIdapplication = nIdapplication;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public String getPath( )
    {
        return _strPath;
    }

    /**
     * Sets path.
     *
     * @param _strPath
     *            the str path
     */
    public void setPath( String _strPath )
    {
        this._strPath = _strPath;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType( )
    {
        return _strType;
    }

    /**
     * Sets type.
     *
     * @param _strType
     *            the str type
     */
    public void setType( String _strType )
    {
        this._strType = _strType;
    }

    /**
     * Gets list properties.
     *
     * @return the list properties
     */
    public List<Properties> getListProperties( )
    {
        return _listProperties;
    }

    /**
     * Sets list properties.
     *
     * @param _listProperties
     *            the list properties
     */
    public void setListProperties( List<Properties> _listProperties )
    {
        this._listProperties = _listProperties;
    }
}
