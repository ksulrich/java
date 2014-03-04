#!/usr/bin/perl -w

$debug = 0;

$file = shift @ARGV;
print "file=$file\n";
unless ($file) {
    $file = "verbrauch.smart_blau";
}
die "Usage: verbrauch.pl [<input-file>]\n" unless (defined($file));
$liter = 0;
$km_last = undef;
$med = 0;
$count = 0;

sub debug {
    my ($stmt) = @_;
    return unless ($debug);
    print $stmt, "\n";
}

open(FD, $file) or die "Can't open $file: $!\n";

while (defined(FD) && ($line = <FD>)) {
    next if ($line =~ /^\#/); 
    next if ($line =~ /^\s*$/);

    chomp($line);
    debug("LINE: $line");
    ($date, $verb, $km, $cost, $full) = split(/\s+/, $line);
    debug("date=$date, verb=$verb, km=$km, cost=$cost, full=$full");
    if (defined($km_last)) {
	debug("km_last=$km_last");
	$diff = $km - $km_last;
	$km_last = $km;
	printf("%s: %.2f Liter; %3.0f km ==> %2.2f\n",
	       $date, $verb, $diff, $verb/$diff*100);
	$med += $verb/$diff*100;
	$count++;
	$liter += $verb;
    } else {
	debug("UNDEF: km_last");
	$km_last = $km;
    }
}
$km_total = $liter/($med/$count)*100;
if ($km_last) {
    $km_total = $km_last;
}
printf("Statistik: =============================================\n" .
       "           Gefahrene Kilometer: %.2f km\n" .
       "           Bisheriger Sprit Verbrauch: %.2f Liter\n" .
       "       ==> Durchschnittsverbrauch: %.2f Liter pro 100 km\n", 
       $km_total, $liter, $med/$count);
