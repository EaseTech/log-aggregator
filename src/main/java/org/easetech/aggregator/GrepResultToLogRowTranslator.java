

package org.easetech.aggregator;

import java.util.ArrayList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.grep4j.core.result.GrepResult;

/**
 * TODO Describe me
 *
 */
public class GrepResultToLogRowTranslator implements Translator<GrepResult , List<LogRow>>{
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    @Override
    public List<LogRow> translate(GrepResult from) {
        List<LogRow> result = new ArrayList<LogRow>();
        
        if (from != null) {
            
            String[] splitStr = from.getText().split("\\r?\\n");
            for(int i=0 ; i<splitStr.length ; i++) {
                LogRow logRow = new LogRow();
                String logText = splitStr[i];
                if(logText != null) {
                    String[] splitText = logText.split("," , 9);
                    try {
                        logRow.setDate(dateFormat.parse(splitText[0]));
                    } catch (ParseException e) {
                        continue;
                    }
                    logRow.setLogType(splitText[1]);
                    logRow.setTransport(splitText[2]);
                    logRow.setLoggingClass(splitText[3]);
                    logRow.setRequestId(splitText[4]);
                    logRow.setService(splitText[5]);
                    logRow.setOperation(splitText[6]);
                    logRow.setInstitution(splitText[7]);
                    logRow.setMessage(splitText[8]);
                    result.add(logRow);
                    
                }
            }
            
            
        }
        return result;
    }

}
