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

#processFile("FormatterMessages.template.java", "../../../descent.ui/src/descent/internal/ui/preferences/formatter/FormatterMessages.java"); 
#processFile("FormatterMessages.template.properties", "../../../descent.ui/src/descent/internal/ui/preferences/formatter/FormatterMessages.properties"); 
#processFile("WhiteSpaceOptions.template.java", "../../../descent.ui/src/descent/internal/ui/preferences/formatter/WhiteSpaceOptions.java"); 
processFile($ARGV[1], $ARGV[2]);

sub processFile
{
	my $evalForEachActive = 0;
	my $evalOnceActive = 0;
	my $evalBlock = "";
	
	open(SRC, $_[0]);
	open(DST, ">" . $_[1]);
	
	while(my $line = <SRC>)
	{
		chomp($line);
		if($evalForEachActive || $evalOnceActive)
		{
			if($line =~ /^\s*?\*\s(.*)$/)
			{
				$evalBlock .= $1 . "\n";
			}
			elsif($line =~ /\*\//)
			{
				if($evalForEachActive)
				{
					foreach(@options)
					{
						eval($evalBlock);
						print $@ if $@;
					}
				}
				else # $evalOnceActive
				{
					eval($evalBlock);
					print $@ if $@;
				}
				$evalBlock = "";
				$evalForEachActive = 0;
				$evalOnceActive = 0;
			}
		}
		elsif($line =~ /\/\*\s*EVAL-FOR-EACH/)
		{
			$evalForEachActive = 1;
		}
		elsif($line =~ /\/\*\s*EVAL-ONCE/)
		{
			$evalOnceActive = 1;
		}
		else
		{
			print DST $line . "\n";
		}
	}
	
	close(SRC);
	close(DST);
}
