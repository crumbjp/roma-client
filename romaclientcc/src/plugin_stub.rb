require 'zlib'
require 'digest/sha1'
require 'roma/async_process'

module Roma
  module CommandPlugin

    module PluginStorage
      include ::Roma::CommandPlugin

      # "set" means "store this data".
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_set(s); set(s); end
      def ev_fset(s); set(s); end

      # get <key>*\r\n
      def ev_get(s); get(s); end

      # fget <key>
      def ev_fget(s); get(s); end

      # gets <key>*\r\n
      def ev_gets(s)
        # @@@@
        # Todo : not implements
      end

      # delete <key> [<time>] [noreply]\r\n
      def ev_delete(s); delete(s); end

      # fdelete <key> [<time>] [noreply]\r\n
      def ev_fdelete(s); delete(s); end

      # rdelete <key> <clock>
      def ev_rdelete(s)
        stub(s,{
               'SERVER_ERROR' => "SERVER_ERROR Some error occurred.\r\n",
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               'DELETED'      => "DELETED\r\n",
               '_'            => "DELETED\r\n"
             })
      end

      # "add" means that "add a new data to a store"
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_add(s); set(s); end
      def ev_fadd(s); set(s); end

      # "replace" means that "replace the previous data with a new one"
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_replace(s); set(s); end
      def ev_freplace(s); set(s); end

      # "append" means that "append a new data to the previous one"
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_append(s); set(s); end
      def ev_fappend(s); set(s); end

      # "prepend" means that "prepend a new data to the previous one"
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_prepend(s); set(s); end
      def ev_fprepend(s); set(s); end


      # "cas" means that "store this data but only if no one else has updated since I last fetched it."
      # <command name> <key> <flags> <exptime> <bytes> <cas-id>[noreply]\r\n
      # <data block>\r\n
      def ev_cas(s); cas(s); end

      def ev_fcas(s); cas(s); end

      # incr <key> <value> [noreply]\r\n
      def ev_incr(s); incdec(s); end
      def ev_fincr(s); incdec(s); end

      # decr <key> <value> [noreply]\r\n
      def ev_decr(s); incdec(s); end
      def ev_fdecr(s); incdec(s); end

      # set_size_of_zredundant <n>
      def ev_set_size_of_zredundant(s)
        # @@@
        # Todo: what ?
      end

      # rset_size_of_zredundant <n>
      def ev_rset_size_of_zredundant(s)
        # @@@
        # Todo: what ?
      end

      private

      def stub(s,rets);
        key,hname = s[1].split("\e")
        key =~ /^([^_]+)_(.*)/
        o = $1
        r = $2
        # o,r = key.split("_")
        if ( o == 'CMD' ) 
          if ( rets[r] == nil )
            send_data(rets['_'])
          else
            send_data(rets[r])
          end
        elsif ( o == 'EXP99' ) 
          if ( s[3].to_i == 99 )
            send_data(rets['_'])
          else
            send_data(rets['SERVER_ERROR'])
          end
        elsif ( o == 'EXP0' ) 
          if ( s[3].to_i == 0 )
            send_data(rets['_'])
          else
            send_data(rets['SERVER_ERROR'])
          end
        elsif ( o == 'TO' ) 
          sleep(r.to_i)
          send_data(rets['_'])
        elsif ( o == 'CLOSE' )
          close_connection
        end
      end
      def set(s)
        if ( s.length < 4 )
          send_data("END\r\n")
          return
        end
        v = read_bytes(s[4].to_i)
        read_bytes(2)
        stub(s,{
               'SERVER_ERROR' => "SERVER_ERROR Some error occurred.\r\n",
               'NOT_STORED'   => "NOT_STORED\r\n",
               'STORED'       => "STORED\r\n",
               '_'            => "STORED\r\n"
             })
      end
      def get(s)
        key,hname = s[1].split("\e")
        stub(s,{
               'SERVER_ERROR' => "SERVER_ERROR Some error occurred.\r\n",
               'ERROR'        => "ERROR\r\n",
               'VALUE'        => "VALUE #{key} 0 6\r\nFOOBAR\r\nEND\r\n",
               '_'            => "END\r\n"
             })
      end

      def delete(s)
        stub(s,{
               'SERVER_ERROR' => "SERVER_ERROR Some error occurred.\r\n",
               'NOT_DELETED'  => "NOT_DELETED\r\n",
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               'DELETED'      => "DELETED\r\n",
               '_'            => "DELETED\r\n"
             })
      end
      def cas(s)
        stub(s,{
               'SERVER_ERROR' => "SERVER_ERROR Some error occurred.\r\n",
               'NOT_STORED'   => "NOT_STORED\r\n",
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               'EXISTS'       => "EXISTS\r\n",
               'STORED'       => "STORED\r\n",
               '_'            => "STORED\r\n"
             })
      end
      def incdec(s)
        stub(s,{
               'SERVER_ERROR' => "SERVER_ERROR Some error occurred.\r\n",
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               '_'            => "10\r\n"
             })
      end

    end # module PluginStorage

  end # module CommandPlugin
end # module Roma
