/**
 * Copyright 2012 CSG@IFI
 * 
 * This file is part of Radiommender.
 * 
 * Radiommender is free software: you can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Radiommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Radiommender. If not, see 
 * http://www.gnu.org/licenses/.
 * 
 */
package org.radiommender.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;


public class LogFormatter extends SimpleFormatter
{
        private final static DateFormat FORMATTER = new SimpleDateFormat("HH:mm:ss.SSS");
        private final static String SEPARATOR = "|";

        @Override
        public String format(java.util.logging.LogRecord record)
        {
                StringBuilder out = new StringBuilder();
                //out.append(Configuration.getDefaultPeerName());
                //out.append(SEPARATOR);
//                out.append(FORMATTER.format(new Date(record.getMillis())));
                out.append(FORMATTER.format(record.getMillis()));
                out.append(SEPARATOR);
                out.append(Thread.currentThread().getName());
                out.append(SEPARATOR);
                out.append(getFromLastDotOn(record.getSourceClassName()));
                out.append(SEPARATOR);
                out.append(record.getLevel());
                out.append(SEPARATOR);
                out.append(record.getSourceMethodName());
                out.append(SEPARATOR);
                out.append(record.getMessage());
                if (record.getThrown() != null)
                {
                        out.append(SEPARATOR);
                        out.append(record.getThrown());
                }
                out.append('\n');
                return out.toString();
        }

        private String getFromLastDotOn(String str)
        {
                int pos = str.lastIndexOf(".");
                return pos > 0 ? str.substring(pos + 1) : str;
        }
}
