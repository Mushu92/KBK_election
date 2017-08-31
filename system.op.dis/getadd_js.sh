#!/bin/bash
# argument[1] : the count of lines
# argument[2] : the list of names

C_name=$1
declare -a array_list
declare -a addr

cmd_getadd='sudo bitcoin-cli -regtest getaddressesbyaccount '$C_name``

#echo $cmd_sed
function get_canaddr() { #(working)
	#start command
	array_list=`$cmd_getadd`
	array_list=($array_list)
        echo  ${array_list[@]}
}

get_canaddr
