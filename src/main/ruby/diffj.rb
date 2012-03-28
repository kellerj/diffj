#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'java'
require 'riel'
require 'diffj/io'

include Java

import org.incava.diffj.Options
import org.incava.diffj.DiffJException
import org.incava.analysis.BriefReport
import org.incava.analysis.DetailedReport

Log::level = Log::DEBUG
Log.set_widths(-15, 5, -50)

class DiffJRuby
  include Loggable
  
  attr_reader :exit_value
  
  def initialize brief, context, highlight, recurse, from_label, fromver, to_label, tover
    @report = brief ? BriefReport.new(java.lang.System.out) : DetailedReport.new(java.lang.System.out, context, highlight)
    @recurseDirectories = recurse
    @from_label = from_label
    @fromver = fromver
    @to_label = to_label
    @tover = tover
    @jef = Java::IO::Factory.new
    @exit_value = 0
  end

  def create_java_element fname, label, source
    begin
      info "fname: #{fname}"
      @jef.create_element java.io.File.new(fname), label, source, @recurseDirectories
    rescue StandardError => de
      $stderr.puts de.message
      @exit_value = 1
      nil
    end
  end
  
  def create_to_element to_name
    create_java_element to_name, @to_label, @tover
  end
  
  def create_from_element from_name
    create_java_element from_name, @from_label, @fromver
  end

  def compare from_name, to_elmt
    begin 
      from_elmt = create_from_element from_name
      return false unless from_elmt
      from_elmt.compare_to @report, to_elmt
      @exit_value = @report.differences.wasAdded ? 1 : 0
      return true;
    rescue DiffJException => de
      $stderr.puts de.message
      @exit_value = 1
      return false
    end
  end

  def process_things names
    if names.size < 2
      $stderr.puts "usage: diffj from-file to-file"
      exit_value = 1
      return
    end

    return unless to_elmt = create_to_element(names[-1])
    names[0 ... -1].each do |fromname|
      compare fromname, to_elmt
    end
  end
end

if __FILE__ == $0
  opts = Options.get
  names = opts.process ARGV
  
  diffj = DiffJRuby.new(opts.showBriefOutput, 
                        opts.showContextOutput, 
                        opts.highlightOutput,
                        opts.recurse,
                        opts.firstFileName, opts.getFromSource,
                        opts.getSecondFileName, opts.getToSource)
  rarray = Array.new
  names.each do |name|
    rarray << name
  end

  diffj.process_things rarray
  puts "exiting with value: #{diffj.exit_value}"
  exit diffj.exit_value
end