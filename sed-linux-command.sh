sed -e "\$a Trailer Record: $(printf "%018d" $(wc -l < your_file.txt))" -e "1i Header Record: $(date +%m/%d/%y)" your_file.txt > output_file.txt
