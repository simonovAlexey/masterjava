<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pref="http://javaops.ru">

    <xsl:output method="html" />
    <xsl:template match="/">
        <html>
            <body>
                <h1>Users</h1>
                <table border="1">
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                    </tr>
                    <xsl:for-each select="pref:Payload/pref:Users/pref:User">
                        <xsl:sort select="."/>
                        <tr>
                            <td>
                                <xsl:value-of select="." />
                            </td>
                            <td>
                                <xsl:value-of select="@email" />
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>