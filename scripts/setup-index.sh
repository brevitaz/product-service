if [ -z "$1" ]
then
   ESHOST='http://3.82.203.10:9200'
else
   ESHOST=$1
fi

if [ -z "$2" ]
then
   INDEX='products'
else
   INDEX=$2
fi

echo $ESHOST

curl -XPUT $ESHOST'/'$INDEX'?pretty' -H 'Content-Type: application/json' -d '
{
  "settings": {
    "analysis": {
      "analyzer": {
        "custom_analyzer": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "stemmer_filter"
          ]
        },
        "keyword_analyzer": {
          "tokenizer": "keyword",
          "filter": [
            "lowercase"
          ]
        }
      },
      "filter": {
        "stemmer_filter": {
          "type": "stemmer",
          "name": "light_english"
        }
      }
    }
  },
  "mappings": {
    "_doc": {
      "properties": {
        "code": {
          "type": "text",
          "analyzer": "keyword_analyzer"
        },
        "reviewScore": {
          "type": "long"
        },
        "author": {
          "type": "text",
          "fields": {
            "keyword": {
              "ignore_above": 256,
              "type": "keyword"
            }
          }
        },
        "specifications": {
            "properties": {
              "colour": {
                "type": "text",
                "analyzer": "custom_analyzer"
              },
              "model": {
                "type": "text",
                "analyzer": "custom_analyzer"
              },
              "storage": {
                "type": "text",
                "analyzer": "custom_analyzer"
              },
              "battery": {
                "type": "text",
                "analyzer": "custom_analyzer"
              },
              "camera": {
                "type": "text",
                "analyzer": "custom_analyzer"
              },
              "modelYear": {
                "type": "long"
              },
              "brand": {
                "type": "text",
                "analyzer": "custom_analyzer"
              }
            }
          },
        "description": {
          "type": "text",
          "analyzer": "custom_analyzer"
        },
        "categories": {
          "type": "text",
          "analyzer": "keyword_analyzer"
        },
        "title": {
          "type": "text",
          "analyzer": "custom_analyzer"
        },
        "tags": {
          "type": "text",
          "analyzer": "keyword_analyzer"
        }
      }
    }
  }
}'
