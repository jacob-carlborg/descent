# This script is used to generate the options for the formatter. Since there are a lot of formatter
# options, which often seem repetitive, I've created this script that, given a template file in a
# specified format, will output the appropriate formatting files.
#
# See the long comment at the beginning of formatter_options.txt for why this script sucks.
#
# Usage : perl formatter_options.pl formatter_options.txt
#
# Written for Descent (http://www.dsource.org/descent) by Robert Fraser (fraserofthenight@gmail.com)
#
use strict;

my @options;

my $filename = $ARGV[0];
unless(-e $filename)
{
	print "Could not open: $filename\n";
	exit 1;
}

open(FILE, $filename);

my $inDefinition = 0;
my %def;
while(my $line = <FILE>)
{
	chomp($line);
	$line =~ s/^#.*$//;
	$line =~ s/^\s*//;
	$line =~ s/\s*$//;
	if($inDefinition)
	{
		if($line =~ /^(.*?)=(.*)$/)
		{
			$def{$1} = $2;
			next;
		}
		else
		{
			$inDefinition = 0;
			my %copy = %def;
			push(@options, \%copy);
			%def = ();
		}
	}
	
	if($line)
	{
		$inDefinition = 1;
		$def{'optName'} = $line;
		$def{'constName'} = "FORMATTER_" . uc($line);
	}
}

close(FILE);

#foreach(@options)
#{
#	while (my ($key, $value) = each(%$_))
#	{
#        print "$key => $value\n";
#    }
#}

processFile("DefaultCodeFormatterOptions.template.java", "../src/descent/internal/formatter/DefaultCodeFormatterOptions.java");
processFile("DefaultCodeFormatterConstants.template.java", "../src/descent/core/formatter/DefaultCodeFormatterConstants.java");
processFile("FormatterMessages.template.java", "../../descent.ui/src/descent/internal/ui/preferences/formatter/FormatterMessages.java");
processFile("FormatterMessages.template.properties", "../../descent.ui/src/descent/internal/ui/preferences/formatter/FormatterMessages.properties");

sub processFile
{
	my $evalForEachActive = 0;
	my $evalBlock = "";
	
	open(SRC, $_[0]);
	open(DST, ">" . $_[1]);
	
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
					eval($evalBlock);
					print $@ if $@;
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
