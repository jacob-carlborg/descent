# This script is used to generate the options for the formatter. Since there are a lot of formatter
# options, which often seem repetitive, I've created this script that, given a template file in a
# specified format, will output the appropriate formatting files. I probably shoud've made this a
# tad bit more general, but it works, so whatever.
#
# Written for Descent (http://www.dsource.org/descent) by Robert Fraser (fraserofthenight@gmail.com)
#
use strict;
use Class::Struct;

struct( Option => {
	optName => '$',    # option name (name used in DefaultCodeFormatterOptions)
	constName => '$',  # constant name (name used in DefaultCodeFormatterConstants)
	default => '$',    # default value
	type => '$'        # Java type
});

my @options;

my $filename = $ARGV[0];
unless(-e $filename)
{
	print "Could not open: $filename\n";
	exit 1;
}

open(FILE, $filename);
	
while(my $line = <FILE>)
{
	unless($line =~ /#/)
	{
		if($line =~ /^([^\s]*?)\s+([^\s]*?)\s+([^\s]*?)\s/)
		{
			my $optName = $2;
			unless($optName =~ /^\s*$/)
			{
				my $default = $3;
				my $type = $1;
				my $constName = "FORMATTER_" . uc($optName);
				my $option = Option->new(optName => $optName, type => $type, default => $default, constName => $constName);
				push(@options, $option);
			}
		}
	}
}

close(FILE);

processFile("DefaultCodeFormatterOptions2.template.java", "../src/descent/internal/formatter/DefaultCodeFormatterOptions2.java");
processFile("DefaultCodeFormatterConstants2.template.java", "../src/descent/core/formatter/DefaultCodeFormatterConstants2.java");

sub processFile
{
	my $evalForEachActive = 0;
	my $evalBlock = "";
	
	open(SRC, $_[0]);
	open(DST, ">" . $_[1]);
	
	print DST "/*\n";
	print DST " * This file has been automatically generated.\n";
	print DST " */\n";
	
	while(my $line = <SRC>)
	{
		chomp($line);
		if($evalForEachActive)
		{
			if($line =~ /^\s*?\*\s(.*)$/)
			{
				$evalBlock .= $1;
			}
			elsif($line =~ /\*\//)
			{
				foreach(@options)
				{
					my $optName = $_->optName;
					my $constName = $_->constName;
					my $type = $_->type;
					my $default = $_->default;
					eval($evalBlock);
				}
				$evalBlock = "";
				$evalForEachActive = 0;
			}
		}
		elsif($line =~ /\/\*\s*EVAL-FOR-EACH/)
		{
			$evalForEachActive = 1;
		}
		else
		{
			print DST $line . "\n";
		}
	}
	
	close(SRC);
	close(DST);
}
