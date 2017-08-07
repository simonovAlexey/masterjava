<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pref="http://javaops.ru">

    <xsl:output method="html"/>

    <xsl:template match="/pref:Payload">
        <html>
            <body>
                <h1>Project groups</h1>
                <xsl:for-each select="pref:Projects/pref:Project">
                    <h3>
                        <xsl:value-of select="@name"/>
                    </h3>
                    <table border="1">
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                        </tr>
                        <xsl:for-each select="pref:Group">
                            <tr>
                                <td>
                                    <xsl:value-of select="@name"/>
                                </td>
                                <td>
                                    <xsl:value-of select="@type"/>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </table>
                    <br/>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>