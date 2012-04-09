#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'diffj/pmdx/function'

class Java::net.sourceforge.pmd.ast::ASTMethodDeclaration
  include DiffJ::AST::Function

  def declarator
    find_child "net.sourceforge.pmd.ast.ASTMethodDeclarator"
  end
  
  def parameters
    declarator.find_child "net.sourceforge.pmd.ast.ASTFormalParameters"
  end

  def name
    declarator.first_token
  end

  def fullname
    to_full_name name, parameters
  end

  def match_score to
    from_name = name.image
    to_name = to.name.image

    return 0 if from_name != to_name
    
    from_params = parameters
    to_params = to.parameters
    
    from_params.match_score to_params
  end
end
