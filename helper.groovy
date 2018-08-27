import groovy.text.StreamingTemplateEngine

def renderTemplate(input, variables) {
  def engine = new StreamingTemplateEngine()
  return engine.createTemplate(input).make(variables).toString()
}

def renderMatrix() {
  //def lst = [];
  lst = ""
  return lst
}
