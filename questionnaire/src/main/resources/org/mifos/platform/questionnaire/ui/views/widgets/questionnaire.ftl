[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[#import "/spring.ftl" as spring /]

[#-- 
A widget to render the UI for collecting questionnaire responses. 

        formBean: Form bean containing the question groups. The convention adopted by this macro 
                  requires the list of question group objects be instances of QuestionGroupDetail and that 
                  the list variable is named "questionGroups" 
    
    formBeanName: Spring binding name of the form bean.
    
      headerText: optional text that appears at the top of the questionnaire form.
--]
[#macro responseForm formBean formBeanName headerText=""]
    <STYLE TYPE="text/css"><!-- @import url(pages/css/datepicker/datepicker.css); --></STYLE>
    <STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/widget.css); --></STYLE>
    <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.min-2.1.2.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.keyfilter-1.7.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.validate.min.js"></script>
    <script type="text/javascript" src="pages/js/datejs/date.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.configuration.js"></script>
    <!--[if IE]><script type="text/javascript" src="pages/js/jquery/jquery.bgiframe.js"></script><![endif]-->
    <script src="pages/questionnaire/js/questionnaire.js" type="text/javascript"></script>
    <span id="page.id" title="questionnaire"></span>
    <div class="questionnaire-widget">
        [#if headerText?has_content]
        <h1>${headerText} - [@spring.message "questionnaire.enterdata"/]</h1>
        [/#if]
        
        [@form.errors "${formBeanName}.*" /]
        <form id="questionnaire" name="questionnaire" action="${flowExecutionUrl}" method="post" class="two-columns">
            [#list formBean.questionGroups as questionGroup]        
                [#list questionGroup.sectionDetails as sectionDetail]
                <!-- section detail -->
                <div class="section-name">
                    <b>${sectionDetail.name}</b>
                </div>
                <fieldset>
                        [#list sectionDetail.questions as question]
                        <!-- question -->
                        <div class='row ${((question_index % 2) == 0)?string("even", "odd")}'>
                            <div class="question">
                                <label class="question" for="questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value">
                                    [#if question.mandatory]<span class="red">*</span>[/#if]
                                    ${question.text}
                                    [#if question.questionType=="DATE"](dd/mm/yyyy)&nbsp[/#if]:
                                </label>
                            </div>
                            <div class="answer">
                            
                                [#switch question.questionType]
                                  [#case "FREETEXT"]
                                    <!-- freetext -->
                                    [@spring.formTextarea "${formBeanName}.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'rows="4" cols="50" maxlength="200"' /]
                                  [#break]
                                  [#case "NUMERIC"]
                                    <!-- numeric -->
                                    [@spring.formInput "${formBeanName}.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'maxlength="30"' /]
                                  [#break]
                                  [#case "DATE"]
                                    <!-- date -->
                                    [@spring.formInput "${formBeanName}.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'maxlength="10" class="date-pick"' /]
                                  [#break]
                                  [#case "MULTI_SELECT"]
                                    <!-- multi-select -->
                                    [@form.checkboxes "${formBeanName}.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices, "<br/>" /]
                                  [#break]
                                  [#case "SMART_SELECT"]
                                  <!-- smart select -->
                                      <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" style="width:21em;" class="txtListSearch"/>
                                        <div class="questionList" id="questionList" style="overflow:auto; width:19em; height:180px; border:1px solid #336699; padding-left:5px">
                                            [@form.checkboxes "${formBeanName}.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,',<br/>', ''/]
                                        </div>
                                  [#break]
                                  [#case "SINGLE_SELECT"]
                                          [#if question.answerChoices?size > 6]
                                          <!-- single select: select -->
                                          [@form.singleSelectWithPrompt "${formBeanName}.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", question.answerChoices, "--selectone--", ''/]
                                          [#else]
                                          <!-- single select: radio -->
                                          [@form.radioButtons "${formBeanName}.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", question.answerChoices,'<br/>', ''/]
                                          [/#if]
                                  [#break]
                                  [#default]
                                     Unknown question type ${question.questionType}
                                [/#switch]
                            </div><!-- answer -->
                        <div class="clear"></div><!-- clear -->
                        </div><!-- row -->
                        [/#list][#-- questions --]
                </fieldset>
                <br/>
                [/#list][#-- sections --]
            [/#list][#-- groups --]
            
            <div class="row">
                [@form.submitButton "questionnaire.continue" "questionsAnswered" /]
                [@form.cancelButton "questionnaire.cancel" "cancel" /]
            </div>
        </form>
    </div>
[/#macro]