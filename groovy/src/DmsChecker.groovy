/*

# Indication of DMS hang
[8/17/11 2:17:59:929 CEST] 000000b9 SchedulerDaem W   SCHD0131W: Task 51 on thread DataServicesWorkManager.Alarm Pool : 1 (00000030) for scheduler DataServicesScheduler (sched/wbm/DataServicesScheduler) has been running for 693516 milliseconds and may be hung.

# Hung finished
[8/17/11 2:29:43:717 CEST] 00000030 ThreadMonitor W   WSVR0606W: Thread "DataServicesWorkManager.Alarm Pool : 1" (00000030) was previously reported to be hung but has completed.  It was active for approximately 1397362 milliseconds.  There is/are 0 thread(s) in total in the server that still may be hung.

# DataServicesScheduler deactivated
[8/18/11 1:32:07:767 CEST] 00000030 DaemonCoordin W   SCHD0132W: Task 51 for scheduler DataServicesScheduler (sched/wbm/DataServicesScheduler) has exceeded the failure threshold limit and has been deactivated.

 */

hungDetectPattern = '.*DataServicesWorkManager.Alarm Pool.*\\(([0-9a-f]+)\\).*has been running for (\\d+) milliseconds and may be hung.*'
hungFinishedPattern = '.*WSVR0606W: Thread "DataServicesWorkManager.Alarm Pool.*\\((\\d+)\\).*was previously reported to be hung but has completed.  It was active for approximately (\\d+) milliseconds'
deactivePattern = '.*SCHD0132W: Task (\\d+) for scheduler DataServicesScheduler.*has exceeded the failure threshold limit and has been deactivated'

for (a in this.args) {
    println "XXX: " + a
    new File(a).eachLine {
        matcher = (it =~ hungDetectPattern)
        if (matcher) {
            // println it
            id = matcher[0][1]
            msg = a + ": Id=" + id + ": DataServicesWorkManager.Alarm thread hung for "
            time =  matcher[0][2] as int
            days = time / 1000 / 60 / 60
            minutes = time / 1000 / 60
            if (days > 1) {
                msg = msg + + days + " days"
            } else {
                msg = msg + minutes + " minutes"
            }
            println msg
        }
        matcher = (it =~ hungFinishedPattern)
        if (matcher) {
            // println it
            id = matcher[0][1]
            msg = a + ": Id=" + id + ": DataServicesWorkManager.Alarm Pool finished after "
            time =  matcher[0][2] as int
            days = time / 1000 / 60 / 60
            minutes = time / 1000 / 60
            if (days > 1) {
                msg = msg + days + " days"
            } else {
                msg = msg + minutes + " minutes"
            }
            println msg
        }
        matcher = (it =~ deactivePattern)
        if (matcher) {
            // println it
            id = matcher[0][1]
            println a + ": Id=" + id + ": DataServicesScheduler deactivated"
        }
    }
    println "----------------------------------------------------"

}
