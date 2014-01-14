/****************************************************************************************************************
*
*  Copyright (c) 2013 OCLC, Inc. All Rights Reserved.
*
*  OCLC proprietary information: the enclosed materials contain
*  proprietary information of OCLC, Inc. and shall not be disclosed in whole or in 
*  any part to any third party or used by any person for any purpose, without written
*  consent of OCLC, Inc.  Duplication of any portion of these materials shall include this notice.
*
******************************************************************************************************************/

package org.easetech.aggregator;

import java.util.List;

/**
 * TODO Describe me
 *
 */
public interface LogInfoData {
    
    String getDescription();
    
    String getComponentName();
    
    List<?> getLogData();

}
