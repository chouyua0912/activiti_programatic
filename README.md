# activiti_programatic
#### activiti spring integration
#### demo to show how to programatically create and deploy a flow

flow of start a process.
RuntimeServiceImpl
 CommandExecutorImpl
  LogInterceptor
   SpringTransactionInterceptor
    CommandContextInterceptor
     CommandInvoker
      StartProcessInstanceCmd
       Find process definition by key from ACT_RE_PROCDEF
       Find deployment by process definition id from ACT_RE_DEPLOYMENT
       Find bpmn byte arrays by deployment id from ACT_GE_BYTEARRAY
